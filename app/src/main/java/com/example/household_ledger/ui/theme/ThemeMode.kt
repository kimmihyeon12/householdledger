package com.example.household_ledger.ui.theme

import android.content.Context
import androidx.compose.runtime.compositionLocalOf

enum class ThemeMode(val label: String, val key: String) {
    SYSTEM("시스템 설정", "system"),
    LIGHT("라이트 모드", "light"),
    DARK("다크 모드", "dark");

    companion object {
        fun fromKey(key: String): ThemeMode =
            entries.find { it.key == key } ?: SYSTEM
    }
}

val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }
val LocalOnThemeModeChange = compositionLocalOf<(ThemeMode) -> Unit> { {} }
val LocalIsDark = compositionLocalOf { false }

object ThemePreferences {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"

    fun getThemeMode(context: Context): ThemeMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.key) ?: ThemeMode.SYSTEM.key
        return ThemeMode.fromKey(key)
    }

    fun setThemeMode(context: Context, mode: ThemeMode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME_MODE, mode.key)
            .apply()
    }
}
