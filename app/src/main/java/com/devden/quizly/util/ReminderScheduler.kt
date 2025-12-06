package com.devden.quizly.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleDailyReminder(hourOfDay: Int = 19, minute: Int = 0) {
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = DailyReminderReceiver.ACTION_DAILY_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun scheduleStreakWarning(hourOfDay: Int = 22, minute: Int = 0, streak: Int) {
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = DailyReminderReceiver.ACTION_STREAK_WARNING
            putExtra(DailyReminderReceiver.EXTRA_STREAK, streak)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_STREAK,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelDailyReminder() {
        val intent = Intent(context, DailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    fun cancelStreakWarning() {
        val intent = Intent(context, DailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_STREAK,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    companion object {
        private const val REQUEST_CODE_DAILY = 1001
        private const val REQUEST_CODE_STREAK = 1002
    }
}

