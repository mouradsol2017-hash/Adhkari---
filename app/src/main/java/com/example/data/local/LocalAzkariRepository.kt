package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CustomDhikrDoc
import com.example.data.model.DailyLogDoc
import com.example.data.model.ReminderDoc
import com.example.data.model.UserSettingsDoc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class LocalAzkariRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    private val _customAthkar = MutableStateFlow(loadCustomAthkar())
    private val _dailyLogs = MutableStateFlow(loadDailyLogs())
    private val _reminders = MutableStateFlow(loadReminders())

    fun observeSettings(): Flow<UserSettingsDoc> = _settings.asStateFlow()
    fun observeCustomAthkar(): Flow<List<CustomDhikrDoc>> = _customAthkar.asStateFlow()
    fun observeDailyLogs(): Flow<List<DailyLogDoc>> = _dailyLogs.asStateFlow()
    fun observeReminders(): Flow<List<ReminderDoc>> = _reminders.asStateFlow()

    fun updateSettings(transform: (UserSettingsDoc) -> UserSettingsDoc) {
        _settings.update { current ->
            val updated = transform(current)
            saveSettings(updated)
            updated
        }
    }

    fun addCustomDhikr(
        title: String,
        arabicText: String,
        translation: String,
        targetCount: Int
    ) {
        val cleanTitle = title.trim().take(120)
        val cleanText = arabicText.trim().take(1500)
        if (cleanTitle.isEmpty() || cleanText.isEmpty()) return
        val newItem = CustomDhikrDoc(
            id = UUID.randomUUID().toString(),
            title = cleanTitle,
            arabicText = cleanText,
            translation = translation.trim().take(1500),
            category = "custom",
            targetCount = targetCount.coerceIn(1, 1000).toLong(),
            favorited = false
        )
        _customAthkar.update { list ->
            val updated = list + newItem
            saveCustomAthkar(updated)
            updated
        }
    }

    fun deleteCustomDhikr(dhikrId: String) {
        _customAthkar.update { list ->
            val updated = list.filterNot { it.id == dhikrId }
            saveCustomAthkar(updated)
            updated
        }
    }

    fun recordDailyProgress(
        dateKey: String,
        addAthkarCount: Long = 0L,
        addTasbihCount: Long = 0L,
        completedDhikrId: String? = null,
        completedCategoryId: String? = null
    ) {
        _dailyLogs.update { list ->
            val existing = list.firstOrNull { it.dateKey == dateKey }
            val updatedLog = if (existing == null) {
                DailyLogDoc(
                    id = dateKey,
                    dateKey = dateKey,
                    athkarCompletedCount = addAthkarCount.coerceIn(0L, 10000L),
                    tasbihCount = addTasbihCount.coerceIn(0L, 1000000L),
                    completedDhikrIds = if (completedDhikrId != null) listOf(completedDhikrId) else emptyList(),
                    completedCategories = if (completedCategoryId != null) listOf(completedCategoryId) else emptyList()
                )
            } else {
                val newDhikrIds = if (completedDhikrId != null && !existing.completedDhikrIds.contains(completedDhikrId)) {
                    (existing.completedDhikrIds + completedDhikrId).take(200)
                } else existing.completedDhikrIds

                val newCats = if (completedCategoryId != null && !existing.completedCategories.contains(completedCategoryId)) {
                    (existing.completedCategories + completedCategoryId).take(20)
                } else existing.completedCategories

                existing.copy(
                    athkarCompletedCount = (existing.athkarCompletedCount + addAthkarCount).coerceIn(0L, 10000L),
                    tasbihCount = (existing.tasbihCount + addTasbihCount).coerceIn(0L, 1000000L),
                    completedDhikrIds = newDhikrIds,
                    completedCategories = newCats
                )
            }
            val updated = list.filterNot { it.dateKey == dateKey } + updatedLog
            saveDailyLogs(updated)
            updated
        }
    }

    fun addReminder(
        title: String,
        categoryId: String,
        hour: Int,
        minute: Int,
        repeatDays: List<Long> = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)
    ) {
        val cleanTitle = title.trim().take(100)
        if (cleanTitle.isEmpty()) return
        val newReminder = ReminderDoc(
            id = UUID.randomUUID().toString(),
            title = cleanTitle,
            categoryId = categoryId.take(40),
            hour = hour.coerceIn(0, 23).toLong(),
            minute = minute.coerceIn(0, 59).toLong(),
            enabled = true,
            repeatDays = repeatDays.take(7)
        )
        _reminders.update { list ->
            val updated = list + newReminder
            saveReminders(updated)
            updated
        }
    }

    fun toggleReminder(reminderId: String, enabled: Boolean) {
        _reminders.update { list ->
            val updated = list.map { if (it.id == reminderId) it.copy(enabled = enabled) else it }
            saveReminders(updated)
            updated
        }
    }

    fun deleteReminder(reminderId: String) {
        _reminders.update { list ->
            val updated = list.filterNot { it.id == reminderId }
            saveReminders(updated)
            updated
        }
    }

    // --- JSON SERIALIZATION ---

    private fun loadSettings(): UserSettingsDoc {
        val raw = prefs.getString(KEY_SETTINGS, null) ?: return UserSettingsDoc()
        return try {
            val obj = JSONObject(raw)
            val favArray = obj.optJSONArray("favoriteIds") ?: JSONArray()
            val favs = (0 until favArray.length()).map { favArray.getString(it) }
            UserSettingsDoc(
                id = "main",
                languageCode = obj.optString("languageCode", "ar"),
                themeMode = obj.optString("themeMode", "system"),
                fontScale = obj.optString("fontScale", "medium"),
                hapticEnabled = obj.optBoolean("hapticEnabled", true),
                favoriteIds = favs,
                tasbihTarget = obj.optLong("tasbihTarget", 33L),
                tasbihPhraseId = obj.optString("tasbihPhraseId", "subhanallah"),
                tasbihCurrentCount = obj.optLong("tasbihCurrentCount", 0L),
                tasbihTotalLifetime = obj.optLong("tasbihTotalLifetime", 0L)
            )
        } catch (_: Exception) {
            UserSettingsDoc()
        }
    }

    private fun saveSettings(s: UserSettingsDoc) {
        val obj = JSONObject().apply {
            put("languageCode", s.languageCode)
            put("themeMode", s.themeMode)
            put("fontScale", s.fontScale)
            put("hapticEnabled", s.hapticEnabled)
            put("favoriteIds", JSONArray(s.favoriteIds))
            put("tasbihTarget", s.tasbihTarget)
            put("tasbihPhraseId", s.tasbihPhraseId)
            put("tasbihCurrentCount", s.tasbihCurrentCount)
            put("tasbihTotalLifetime", s.tasbihTotalLifetime)
        }
        prefs.edit().putString(KEY_SETTINGS, obj.toString()).apply()
    }

    private fun loadCustomAthkar(): List<CustomDhikrDoc> {
        val raw = prefs.getString(KEY_CUSTOM_ATHKAR, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { idx ->
                val o = arr.getJSONObject(idx)
                CustomDhikrDoc(
                    id = o.optString("id"),
                    title = o.optString("title"),
                    arabicText = o.optString("arabicText"),
                    translation = o.optString("translation"),
                    category = o.optString("category", "custom"),
                    targetCount = o.optLong("targetCount", 1L),
                    favorited = o.optBoolean("favorited", false)
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveCustomAthkar(items: List<CustomDhikrDoc>) {
        val arr = JSONArray()
        items.forEach { d ->
            arr.put(
                JSONObject().apply {
                    put("id", d.id)
                    put("title", d.title)
                    put("arabicText", d.arabicText)
                    put("translation", d.translation)
                    put("category", d.category)
                    put("targetCount", d.targetCount)
                    put("favorited", d.favorited)
                }
            )
        }
        prefs.edit().putString(KEY_CUSTOM_ATHKAR, arr.toString()).apply()
    }

    private fun loadDailyLogs(): List<DailyLogDoc> {
        val raw = prefs.getString(KEY_DAILY_LOGS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { idx ->
                val o = arr.getJSONObject(idx)
                val idsArr = o.optJSONArray("completedDhikrIds") ?: JSONArray()
                val catsArr = o.optJSONArray("completedCategories") ?: JSONArray()
                DailyLogDoc(
                    id = o.optString("dateKey"),
                    dateKey = o.optString("dateKey"),
                    athkarCompletedCount = o.optLong("athkarCompletedCount", 0L),
                    tasbihCount = o.optLong("tasbihCount", 0L),
                    completedDhikrIds = (0 until idsArr.length()).map { idsArr.getString(it) },
                    completedCategories = (0 until catsArr.length()).map { catsArr.getString(it) }
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveDailyLogs(logs: List<DailyLogDoc>) {
        val arr = JSONArray()
        logs.forEach { log ->
            arr.put(
                JSONObject().apply {
                    put("dateKey", log.dateKey)
                    put("athkarCompletedCount", log.athkarCompletedCount)
                    put("tasbihCount", log.tasbihCount)
                    put("completedDhikrIds", JSONArray(log.completedDhikrIds))
                    put("completedCategories", JSONArray(log.completedCategories))
                }
            )
        }
        prefs.edit().putString(KEY_DAILY_LOGS, arr.toString()).apply()
    }

    private fun loadReminders(): List<ReminderDoc> {
        val raw = prefs.getString(KEY_REMINDERS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { idx ->
                val o = arr.getJSONObject(idx)
                val daysArr = o.optJSONArray("repeatDays") ?: JSONArray()
                ReminderDoc(
                    id = o.optString("id"),
                    title = o.optString("title"),
                    categoryId = o.optString("categoryId", "morning"),
                    hour = o.optLong("hour", 6L),
                    minute = o.optLong("minute", 30L),
                    enabled = o.optBoolean("enabled", true),
                    repeatDays = (0 until daysArr.length()).map { daysArr.getLong(it) }
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveReminders(reminders: List<ReminderDoc>) {
        val arr = JSONArray()
        reminders.forEach { r ->
            arr.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("title", r.title)
                    put("categoryId", r.categoryId)
                    put("hour", r.hour)
                    put("minute", r.minute)
                    put("enabled", r.enabled)
                    put("repeatDays", JSONArray(r.repeatDays))
                }
            )
        }
        prefs.edit().putString(KEY_REMINDERS, arr.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "azkari_local_store"
        private const val KEY_SETTINGS = "settings_json"
        private const val KEY_CUSTOM_ATHKAR = "custom_athkar_json"
        private const val KEY_DAILY_LOGS = "daily_logs_json"
        private const val KEY_REMINDERS = "reminders_json"
    }
}
