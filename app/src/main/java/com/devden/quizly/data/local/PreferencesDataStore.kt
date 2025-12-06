package com.devden.quizly.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val SOUND_ENABLED_KEY = booleanPreferencesKey("sound_enabled")
        private val HAPTIC_ENABLED_KEY = booleanPreferencesKey("haptic_enabled")
        private val DAILY_REMINDER_KEY = booleanPreferencesKey("daily_reminder")
        private val STREAK_ALERT_KEY = booleanPreferencesKey("streak_alert")
        private val ACHIEVEMENT_NOTIF_KEY = booleanPreferencesKey("achievement_notif")
    }

    val themeMode: Flow<String> = context.prefsDataStore.data.map { preferences ->
        preferences[THEME_MODE_KEY] ?: "SYSTEM"
    }

    val soundEnabled: Flow<Boolean> = context.prefsDataStore.data.map { preferences ->
        preferences[SOUND_ENABLED_KEY] ?: true
    }

    val hapticEnabled: Flow<Boolean> = context.prefsDataStore.data.map { preferences ->
        preferences[HAPTIC_ENABLED_KEY] ?: true
    }

    val dailyReminderEnabled: Flow<Boolean> = context.prefsDataStore.data.map { preferences ->
        preferences[DAILY_REMINDER_KEY] ?: true
    }

    val streakAlertEnabled: Flow<Boolean> = context.prefsDataStore.data.map { preferences ->
        preferences[STREAK_ALERT_KEY] ?: true
    }

    val achievementNotifEnabled: Flow<Boolean> = context.prefsDataStore.data.map { preferences ->
        preferences[ACHIEVEMENT_NOTIF_KEY] ?: true
    }

    suspend fun saveThemeMode(mode: String) {
        context.prefsDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    suspend fun saveSoundEnabled(enabled: Boolean) {
        context.prefsDataStore.edit { preferences ->
            preferences[SOUND_ENABLED_KEY] = enabled
        }
    }

    suspend fun saveHapticEnabled(enabled: Boolean) {
        context.prefsDataStore.edit { preferences ->
            preferences[HAPTIC_ENABLED_KEY] = enabled
        }
    }

    suspend fun saveDailyReminderEnabled(enabled: Boolean) {
        context.prefsDataStore.edit { preferences ->
            preferences[DAILY_REMINDER_KEY] = enabled
        }
    }

    suspend fun saveStreakAlertEnabled(enabled: Boolean) {
        context.prefsDataStore.edit { preferences ->
            preferences[STREAK_ALERT_KEY] = enabled
        }
    }

    suspend fun saveAchievementNotifEnabled(enabled: Boolean) {
        context.prefsDataStore.edit { preferences ->
            preferences[ACHIEVEMENT_NOTIF_KEY] = enabled
        }
    }
}

