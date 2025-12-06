package com.devden.quizly.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val soundManager: com.devden.quizly.util.SoundManager,
    private val notificationManager: com.devden.quizly.util.NotificationManager,
    private val reminderScheduler: com.devden.quizly.util.ReminderScheduler,
    private val preferencesDataStore: com.devden.quizly.data.local.PreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Load current settings
        viewModelScope.launch {
            preferencesDataStore.themeMode.collect { mode ->
                _uiState.value = _uiState.value.copy(
                    themeMode = ThemeMode.valueOf(mode),
                    soundEnabled = soundManager.soundEnabled,
                    hapticEnabled = soundManager.vibrationEnabled
                )
            }
        }
    }

    fun onThemeChanged(theme: ThemeMode) {
        _uiState.value = _uiState.value.copy(themeMode = theme)
        viewModelScope.launch {
            preferencesDataStore.saveThemeMode(theme.name)
        }
    }

    fun onNotificationToggle(type: NotificationType, enabled: Boolean) {
        val notifications = _uiState.value.notifications.toMutableMap()
        notifications[type] = enabled
        _uiState.value = _uiState.value.copy(notifications = notifications)

        // Handle notification scheduling
        when (type) {
            NotificationType.DAILY_REMINDER -> {
                if (enabled) {
                    reminderScheduler.scheduleDailyReminder(hourOfDay = 19, minute = 0)
                } else {
                    reminderScheduler.cancelDailyReminder()
                }
            }
            NotificationType.STREAK_ALERT -> {
                if (enabled) {
                    reminderScheduler.scheduleStreakWarning(hourOfDay = 22, minute = 0, streak = 0)
                } else {
                    reminderScheduler.cancelStreakWarning()
                }
            }
            NotificationType.ACHIEVEMENTS -> {
                // Achievement notifications are event-based, not scheduled
            }
        }

        // TODO: Save to DataStore
    }

    fun onSoundToggle(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(soundEnabled = enabled)
        soundManager.soundEnabled = enabled

        // Play a preview sound when enabled
        if (enabled) {
            soundManager.playCorrectSound()
        }

        viewModelScope.launch {
            preferencesDataStore.saveSoundEnabled(enabled)
        }
    }

    fun onHapticToggle(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(hapticEnabled = enabled)
        soundManager.vibrationEnabled = enabled

        // Play a preview vibration when enabled
        if (enabled) {
            soundManager.vibrateLight()
        }

        viewModelScope.launch {
            preferencesDataStore.saveHapticEnabled(enabled)
        }
    }

    fun onClearHistory() {
        viewModelScope.launch {
            // TODO: Clear history from DataStore
            _uiState.value = _uiState.value.copy(showClearHistoryDialog = false)
        }
    }

    fun onResetProgress() {
        viewModelScope.launch {
            // TODO: Reset all progress from DataStore
            _uiState.value = _uiState.value.copy(showResetProgressDialog = false)
        }
    }

    fun showClearHistoryDialog() {
        _uiState.value = _uiState.value.copy(showClearHistoryDialog = true)
    }

    fun dismissClearHistoryDialog() {
        _uiState.value = _uiState.value.copy(showClearHistoryDialog = false)
    }

    fun showResetProgressDialog() {
        _uiState.value = _uiState.value.copy(showResetProgressDialog = true)
    }

    fun dismissResetProgressDialog() {
        _uiState.value = _uiState.value.copy(showResetProgressDialog = false)
    }

    fun toggleSection(section: SettingsSection) {
        val expanded = _uiState.value.expandedSections.toMutableSet()
        if (expanded.contains(section)) {
            expanded.remove(section)
        } else {
            expanded.add(section)
        }
        _uiState.value = _uiState.value.copy(expandedSections = expanded)
    }
}

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notifications: Map<NotificationType, Boolean> = mapOf(
        NotificationType.DAILY_REMINDER to true,
        NotificationType.STREAK_ALERT to true,
        NotificationType.ACHIEVEMENTS to true
    ),
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val showClearHistoryDialog: Boolean = false,
    val showResetProgressDialog: Boolean = false,
    val expandedSections: Set<SettingsSection> = setOf(
        SettingsSection.APPEARANCE,
        SettingsSection.NOTIFICATIONS,
        SettingsSection.DATA,
        SettingsSection.ABOUT
    )
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class NotificationType(val displayName: String) {
    DAILY_REMINDER("Daily Quiz Reminder"),
    STREAK_ALERT("Streak Alerts"),
    ACHIEVEMENTS("Achievement Notifications")
}

enum class SettingsSection(val displayName: String, val icon: String) {
    APPEARANCE("Appearance", "🎨"),
    NOTIFICATIONS("Notifications", "🔔"),
    DATA("Data Management", "💾"),
    ABOUT("About", "ℹ️")
}

