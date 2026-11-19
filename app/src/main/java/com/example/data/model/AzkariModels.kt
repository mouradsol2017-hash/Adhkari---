package com.example.data.model

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class AppLanguage(val code: String, val nativeLabel: String, val isRtl: Boolean) {
    AR("ar", "العربية", true),
    EN("en", "English", false),
    FR("fr", "Français", false);

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: AR
    }
}

enum class ThemePreference(val code: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromCode(code: String?): ThemePreference =
            entries.firstOrNull { it.code == code } ?: SYSTEM
    }
}

enum class FontScalePreference(val code: String, val multiplier: Float) {
    SMALL("small", 0.88f),
    MEDIUM("medium", 1.0f),
    LARGE("large", 1.18f),
    XLARGE("xlarge", 1.38f);

    companion object {
        fun fromCode(code: String?): FontScalePreference =
            entries.firstOrNull { it.code == code } ?: MEDIUM
    }
}

enum class DhikrCategory(val id: String) {
    MORNING("morning"),
    EVENING("evening"),
    AFTER_PRAYER("after_prayer"),
    SLEEP("sleep"),
    WAKING_UP("waking_up"),
    GENERAL("general"),
    FAVORITES("favorites"),
    CUSTOM("custom");

    companion object {
        fun fromId(id: String?): DhikrCategory =
            entries.firstOrNull { it.id == id } ?: GENERAL
    }
}

data class DhikrItem(
    val id: String,
    val category: DhikrCategory,
    val titleAr: String,
    val titleEn: String,
    val titleFr: String,
    val arabicText: String,
    val transliteration: String,
    val translationEn: String,
    val translationFr: String,
    val virtueAr: String,
    val virtueEn: String,
    val virtueFr: String,
    val targetCount: Int,
    val isCustom: Boolean = false
) {
    fun localizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> titleAr
        AppLanguage.EN -> titleEn.ifBlank { titleAr }
        AppLanguage.FR -> titleFr.ifBlank { titleEn.ifBlank { titleAr } }
    }

    fun localizedTranslation(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> ""
        AppLanguage.EN -> translationEn
        AppLanguage.FR -> translationFr.ifBlank { translationEn }
    }

    fun localizedVirtue(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> virtueAr
        AppLanguage.EN -> virtueEn.ifBlank { virtueAr }
        AppLanguage.FR -> virtueFr.ifBlank { virtueEn.ifBlank { virtueAr } }
    }
}

data class TasbihPhrase(
    val id: String,
    val arabic: String,
    val transliteration: String,
    val translationEn: String,
    val translationFr: String,
    val defaultTarget: Int = 33
)

data class AchievementBadge(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val titleFr: String,
    val descAr: String,
    val descEn: String,
    val descFr: String,
    val iconKey: String,
    val unlocked: Boolean,
    val progress: Float
) {
    fun localizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> titleAr
        AppLanguage.EN -> titleEn
        AppLanguage.FR -> titleFr
    }

    fun localizedDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> descAr
        AppLanguage.EN -> descEn
        AppLanguage.FR -> descFr
    }
}

data class UserSettingsDoc(
    val id: String = "main",
    val languageCode: String = "ar",
    val themeMode: String = "system",
    val fontScale: String = "medium",
    val hapticEnabled: Boolean = true,
    val favoriteIds: List<String> = emptyList(),
    val tasbihTarget: Long = 33L,
    val tasbihPhraseId: String = "subhanallah",
    val tasbihCurrentCount: Long = 0L,
    val tasbihTotalLifetime: Long = 0L
)

data class CustomDhikrDoc(
    val id: String = "",
    val title: String = "",
    val arabicText: String = "",
    val translation: String = "",
    val category: String = "custom",
    val targetCount: Long = 1L,
    val favorited: Boolean = false
)

data class DailyLogDoc(
    val id: String = "",
    val dateKey: String = "",
    val athkarCompletedCount: Long = 0L,
    val tasbihCount: Long = 0L,
    val completedDhikrIds: List<String> = emptyList(),
    val completedCategories: List<String> = emptyList()
)

data class ReminderDoc(
    val id: String = "",
    val title: String = "",
    val categoryId: String = "morning",
    val hour: Long = 6L,
    val minute: Long = 30L,
    val enabled: Boolean = true,
    val repeatDays: List<Long> = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)
)
