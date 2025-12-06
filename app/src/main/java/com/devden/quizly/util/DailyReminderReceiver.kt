package com.devden.quizly.util

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DailyReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_DAILY_REMINDER -> {
                notificationManager.showDailyQuizReminder()
            }
            ACTION_STREAK_WARNING -> {
                val streak = intent.getIntExtra(EXTRA_STREAK, 0)
                notificationManager.showStreakAlert(streak, isAboutToBreak = true)
            }
        }
    }

    companion object {
        const val ACTION_DAILY_REMINDER = "com.devden.quizly.DAILY_REMINDER"
        const val ACTION_STREAK_WARNING = "com.devden.quizly.STREAK_WARNING"
        const val EXTRA_STREAK = "extra_streak"
    }
}

