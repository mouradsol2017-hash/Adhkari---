package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.catalog.BuiltInAthkarCatalog
import com.example.data.local.LocalAzkariRepository
import com.example.data.model.AchievementBadge
import com.example.data.model.AppLanguage
import com.example.data.model.CustomDhikrDoc
import com.example.data.model.DailyLogDoc
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.data.model.FontScalePreference
import com.example.data.model.ReminderDoc
import com.example.data.model.ThemePreference
import com.example.data.model.UserSettingsDoc
import com.example.notifications.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyChartPoint(
    val label: String,
    val dateKey: String,
    val athkarCount: Int,
    val tasbihCount: Int
)

data class StatsSummary(
    val totalAthkar: Int = 0,
    val totalTasbih: Long = 0L,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val level: Int = 1,
    val xp: Int = 0,
    val levelProgress: Float = 0f,
    val weeklyPoints: List<DailyChartPoint> = emptyList(),
    val monthlyPoints: List<DailyChartPoint> = emptyList(),
    val achievements: List<AchievementBadge> = emptyList()
)

data class AzkariUiState(
    val settings: UserSettingsDoc = UserSettingsDoc(),
    val allAthkar: List<DhikrItem> = BuiltInAthkarCatalog.builtInAthkar,
    val customAthkar: List<CustomDhikrDoc> = emptyList(),
    val dailyLogs: List<DailyLogDoc> = emptyList(),
    val reminders: List<ReminderDoc> = emptyList(),
    val todayCompletedIds: Set<String> = emptySet(),
    val sessionCounts: Map<String, Int> = emptyMap(),
    val searchQuery: String = "",
    val stats: StatsSummary = StatsSummary(),
    val snackbarMessage: String? = null
) {
    val language: AppLanguage get() = AppLanguage.fromCode(settings.languageCode)
    val themePreference: ThemePreference get() = ThemePreference.fromCode(settings.themeMode)
    val fontScale: FontScalePreference get() = FontScalePreference.fromCode(settings.fontScale)
    val favoriteIds: Set<String> get() = settings.favoriteIds.toSet()

    val searchResults: List<DhikrItem>
        get() {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) return emptyList()
            return allAthkar.filter { item ->
                item.titleAr.contains(q, ignoreCase = true) ||
                    item.titleEn.contains(q, ignoreCase = true) ||
                    item.titleFr.contains(q, ignoreCase = true) ||
                    item.arabicText.contains(q, ignoreCase = true) ||
                    item.translationEn.contains(q, ignoreCase = true) ||
                    item.translationFr.contains(q, ignoreCase = true)
            }
        }

    fun athkarForCategory(category: DhikrCategory): List<DhikrItem> = when (category) {
        DhikrCategory.FAVORITES -> allAthkar.filter { favoriteIds.contains(it.id) }
        DhikrCategory.CUSTOM -> allAthkar.filter { it.isCustom }
        else -> allAthkar.filter { it.category == category }
    }
}

class AzkariViewModel(
    private val repository: LocalAzkariRepository,
    private val appContext: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sessionCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    private data class StoredSnapshot(
        val settings: UserSettingsDoc,
        val customAthkar: List<CustomDhikrDoc>,
        val dailyLogs: List<DailyLogDoc>,
        val reminders: List<ReminderDoc>
    )

    private val storedState = combine(
        repository.observeSettings(),
        repository.observeCustomAthkar(),
        repository.observeDailyLogs(),
        repository.observeReminders().onEach { list ->
            ReminderScheduler.syncReminders(appContext, list)
        }
    ) { settings, customs, logs, reminders ->
        StoredSnapshot(settings, customs, logs, reminders)
    }

    val uiState: StateFlow<AzkariUiState> = combine(
        storedState,
        _searchQuery,
        _sessionCounts,
        _snackbarMessage
    ) { stored, query, counts, snack ->
        val mappedCustom = stored.customAthkar.map { doc ->
            DhikrItem(
                id = "custom_${doc.id}",
                category = DhikrCategory.CUSTOM,
                titleAr = doc.title,
                titleEn = doc.title,
                titleFr = doc.title,
                arabicText = doc.arabicText,
                transliteration = "",
                translationEn = doc.translation,
                translationFr = doc.translation,
                virtueAr = "",
                virtueEn = "",
                virtueFr = "",
                targetCount = doc.targetCount.toInt().coerceAtLeast(1),
                isCustom = true
            )
        }
        val all = BuiltInAthkarCatalog.builtInAthkar + mappedCustom
        val todayKey = todayDateKey()
        val todayLog = stored.dailyLogs.firstOrNull { it.dateKey == todayKey }
        val stats = computeStats(stored.dailyLogs, stored.settings, stored.customAthkar.size)
        AzkariUiState(
            settings = stored.settings,
            allAthkar = all,
            customAthkar = stored.customAthkar,
            dailyLogs = stored.dailyLogs,
            reminders = stored.reminders,
            todayCompletedIds = todayLog?.completedDhikrIds?.toSet() ?: emptySet(),
            sessionCounts = counts,
            searchQuery = query,
            stats = stats,
            snackbarMessage = snack
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), AzkariUiState())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // --- DHIKR READING & FAVORITES ---

    fun incrementDhikrCount(item: DhikrItem): Boolean {
        val current = _sessionCounts.value[item.id] ?: 0
        if (current >= item.targetCount) return true
        val next = current + 1
        _sessionCounts.update { it + (item.id to next) }
        val completedNow = next >= item.targetCount
        if (completedNow) {
            repository.recordDailyProgress(
                dateKey = todayDateKey(),
                addAthkarCount = 1L,
                completedDhikrId = item.id,
                completedCategoryId = item.category.id
            )
        }
        return completedNow
    }

    fun resetCategorySession(items: List<DhikrItem>) {
        _sessionCounts.update { map ->
            map.toMutableMap().apply {
                items.forEach { remove(it.id) }
            }
        }
    }

    fun toggleFavorite(dhikrId: String) {
        repository.updateSettings { s ->
            val favs = s.favoriteIds.toMutableList()
            if (favs.contains(dhikrId)) favs.remove(dhikrId)
            else if (favs.size < 200) favs.add(dhikrId)
            s.copy(favoriteIds = favs)
        }
    }

    fun addCustomDhikr(title: String, arabicText: String, translation: String, targetCount: Int) {
        repository.addCustomDhikr(title, arabicText, translation, targetCount)
    }

    fun deleteCustomDhikr(docId: String) {
        repository.deleteCustomDhikr(docId)
    }

    // --- EYES-FREE TASBIH ---

    fun incrementTasbih(): Boolean {
        val s = uiState.value.settings
        val target = s.tasbihTarget.coerceAtLeast(1L)
        val rawNext = s.tasbihCurrentCount + 1L
        val cycleCompleted = target < 9999L && rawNext >= target
        val nextCycleCount = if (cycleCompleted) 0L else rawNext
        val nextLifetime = s.tasbihTotalLifetime + 1L

        repository.updateSettings { cur ->
            cur.copy(
                tasbihCurrentCount = nextCycleCount,
                tasbihTotalLifetime = nextLifetime
            )
        }
        repository.recordDailyProgress(
            dateKey = todayDateKey(),
            addTasbihCount = 1L
        )
        return cycleCompleted
    }

    fun resetTasbihCurrent() {
        repository.updateSettings { it.copy(tasbihCurrentCount = 0L) }
    }

    fun selectTasbihPhrase(phraseId: String, defaultTarget: Int) {
        repository.updateSettings {
            it.copy(
                tasbihPhraseId = phraseId,
                tasbihTarget = defaultTarget.toLong(),
                tasbihCurrentCount = 0L
            )
        }
    }

    fun setTasbihTarget(target: Long) {
        repository.updateSettings { it.copy(tasbihTarget = target.coerceIn(1L, 10000L)) }
    }

    // --- SETTINGS & BACKUP ---

    fun setLanguage(lang: AppLanguage) {
        repository.updateSettings { it.copy(languageCode = lang.code) }
    }

    fun setThemePreference(pref: ThemePreference) {
        repository.updateSettings { it.copy(themeMode = pref.code) }
    }

    fun setFontScale(scale: FontScalePreference) {
        repository.updateSettings { it.copy(fontScale = scale.code) }
    }

    fun setHapticEnabled(enabled: Boolean) {
        repository.updateSettings { it.copy(hapticEnabled = enabled) }
    }

    fun showSavedConfirmation(message: String) {
        _snackbarMessage.value = message
    }

    fun buildSettingsBackupJson(): String {
        val state = uiState.value
        val s = state.settings
        return JSONObject().apply {
            put("app", "Azkari")
            put("exportedAt", todayDateKey())
            put("languageCode", s.languageCode)
            put("themeMode", s.themeMode)
            put("fontScale", s.fontScale)
            put("hapticEnabled", s.hapticEnabled)
            put("favoriteIds", JSONArray(s.favoriteIds))
            put("tasbihTarget", s.tasbihTarget)
            put("tasbihTotalLifetime", s.tasbihTotalLifetime)
            put("customAthkarCount", state.customAthkar.size)
            put("remindersCount", state.reminders.size)
        }.toString(2)
    }

    // --- REMINDERS ---

    fun addReminder(title: String, categoryId: String, hour: Int, minute: Int) {
        repository.addReminder(title, categoryId, hour, minute)
    }

    fun toggleReminder(reminderId: String, enabled: Boolean) {
        repository.toggleReminder(reminderId, enabled)
    }

    fun deleteReminder(reminderId: String) {
        ReminderScheduler.cancel(appContext, reminderId)
        repository.deleteReminder(reminderId)
    }

    // --- STATS & ACHIEVEMENTS ENGINE ---

    private fun computeStats(
        logs: List<DailyLogDoc>,
        settings: UserSettingsDoc,
        customCount: Int
    ): StatsSummary {
        val byDate = logs.associateBy { it.dateKey }
        val totalAthkar = logs.sumOf { it.athkarCompletedCount.toInt() }
        val totalTasbih = maxOf(settings.tasbihTotalLifetime, logs.sumOf { it.tasbihCount })

        val activeDates = logs
            .filter { it.athkarCompletedCount > 0L || it.tasbihCount > 0L }
            .map { it.dateKey }
            .toSet()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        var currentStreak = 0
        val todayStr = sdf.format(cal.time)
        if (!activeDates.contains(todayStr)) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        while (activeDates.contains(sdf.format(cal.time))) {
            currentStreak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        val sortedDates = activeDates.sorted()
        var bestStreak = 0
        var run = 0
        var prevDate: Date? = null
        for (dStr in sortedDates) {
            val parsed = try { sdf.parse(dStr) } catch (_: Exception) { null } ?: continue
            if (prevDate != null) {
                val diffDays = ((parsed.time - prevDate.time) / (1000L * 60 * 60 * 24)).toInt()
                run = if (diffDays == 1) run + 1 else 1
            } else {
                run = 1
            }
            if (run > bestStreak) bestStreak = run
            prevDate = parsed
        }
        bestStreak = maxOf(bestStreak, currentStreak)

        val dayFmt = SimpleDateFormat("EEE", Locale.getDefault())
        val shortDayFmt = SimpleDateFormat("dd", Locale.US)
        val weekly = (6 downTo 0).map { offset ->
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
            val key = sdf.format(c.time)
            val doc = byDate[key]
            DailyChartPoint(
                label = dayFmt.format(c.time).take(3),
                dateKey = key,
                athkarCount = doc?.athkarCompletedCount?.toInt() ?: 0,
                tasbihCount = doc?.tasbihCount?.toInt() ?: 0
            )
        }
        val monthly = (13 downTo 0).map { offset ->
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
            val key = sdf.format(c.time)
            val doc = byDate[key]
            DailyChartPoint(
                label = shortDayFmt.format(c.time),
                dateKey = key,
                athkarCount = doc?.athkarCompletedCount?.toInt() ?: 0,
                tasbihCount = doc?.tasbihCount?.toInt() ?: 0
            )
        }

        val xp = totalAthkar * 15 + totalTasbih.toInt() + currentStreak * 25
        val level = (xp / 250) + 1
        val levelProgress = ((xp % 250) / 250f).coerceIn(0f, 1f)

        val hasMorningAndEvening = logs.any {
            it.completedCategories.contains(DhikrCategory.MORNING.id) &&
                it.completedCategories.contains(DhikrCategory.EVENING.id)
        }

        val badges = listOf(
            AchievementBadge(
                id = "first_step",
                titleAr = "بداية مباركة",
                titleEn = "Blessed Start",
                titleFr = "Début Béni",
                descAr = "أكمل أول ذكر يومي لك",
                descEn = "Complete your first daily Dhikr",
                descFr = "Terminez votre premier Dhikr quotidien",
                iconKey = "spark",
                unlocked = totalAthkar >= 1,
                progress = (totalAthkar / 1f).coerceIn(0f, 1f)
            ),
            AchievementBadge(
                id = "tasbih_100",
                titleAr = "الذاكر المسبّح",
                titleEn = "Century of Tasbih",
                titleFr = "Cent Tasbih",
                descAr = "بلوغ 100 تسبيحة",
                descEn = "Reach 100 Tasbih counts",
                descFr = "Atteindre 100 Tasbih",
                iconKey = "tasbih",
                unlocked = totalTasbih >= 100L,
                progress = (totalTasbih / 100f).coerceIn(0f, 1f)
            ),
            AchievementBadge(
                id = "streak_7",
                titleAr = "مواظب 7 أيام",
                titleEn = "7-Day Guardian",
                titleFr = "Série de 7 jours",
                descAr = "استمر في الذكر 7 أيام متتالية",
                descEn = "Maintain a 7-day remembrance streak",
                descFr = "Maintenir une série de 7 jours",
                iconKey = "streak",
                unlocked = bestStreak >= 7,
                progress = (bestStreak / 7f).coerceIn(0f, 1f)
            ),
            AchievementBadge(
                id = "morning_evening",
                titleAr = "نور الصباح والمساء",
                titleEn = "Dawn & Dusk Light",
                titleFr = "Lumière Matin & Soir",
                descAr = "أتمّ أذكار الصباح والمساء في يوم واحد",
                descEn = "Complete both Morning & Evening Athkar in one day",
                descFr = "Complétez Matin & Soir dans la même journée",
                iconKey = "sun_moon",
                unlocked = hasMorningAndEvening,
                progress = if (hasMorningAndEvening) 1f else 0f
            ),
            AchievementBadge(
                id = "tasbih_1000",
                titleAr = "ألف تسبيحة",
                titleEn = "Thousand Praises",
                titleFr = "Mille Louanges",
                descAr = "بلوغ 1,000 تسبيحة تراكمية",
                descEn = "Reach 1,000 lifetime Tasbih counts",
                descFr = "Atteindre 1 000 Tasbih au total",
                iconKey = "crown",
                unlocked = totalTasbih >= 1000L,
                progress = (totalTasbih / 1000f).coerceIn(0f, 1f)
            ),
            AchievementBadge(
                id = "custom_author",
                titleAr = "وِردي الخاص",
                titleEn = "Personal Wird",
                titleFr = "Wird Personnel",
                descAr = "أضف ذكراً مخصصاً لقائمتك",
                descEn = "Create your own custom Dhikr",
                descFr = "Ajoutez votre propre Dhikr personnalisé",
                iconKey = "heart",
                unlocked = customCount >= 1,
                progress = (customCount / 1f).coerceIn(0f, 1f)
            )
        )

        return StatsSummary(
            totalAthkar = totalAthkar,
            totalTasbih = totalTasbih,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            level = level,
            xp = xp,
            levelProgress = levelProgress,
            weeklyPoints = weekly,
            monthlyPoints = monthly,
            achievements = badges
        )
    }

    private fun todayDateKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val appCtx = context.applicationContext
            return AzkariViewModel(LocalAzkariRepository(appCtx), appCtx) as T
        }
    }
}
