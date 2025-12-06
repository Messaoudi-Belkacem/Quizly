package com.devden.quizly.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devden.quizly.MainActivity
import com.devden.quizly.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CHANNEL_ID_DAILY = "daily_quiz_channel"
        private const val CHANNEL_ID_STREAK = "streak_alert_channel"
        private const val CHANNEL_ID_ACHIEVEMENT = "achievement_channel"

        private const val NOTIFICATION_ID_DAILY = 1001
        private const val NOTIFICATION_ID_STREAK = 1002
        private const val NOTIFICATION_ID_ACHIEVEMENT = 1003
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Daily Quiz Channel
            val dailyChannel = NotificationChannel(
                CHANNEL_ID_DAILY,
                "Daily Quiz Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to take your daily quiz"
                enableVibration(true)
                enableLights(true)
            }

            // Streak Alert Channel
            val streakChannel = NotificationChannel(
                CHANNEL_ID_STREAK,
                "Streak Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts about your quiz streak status"
                enableVibration(true)
                enableLights(true)
            }

            // Achievement Channel
            val achievementChannel = NotificationChannel(
                CHANNEL_ID_ACHIEVEMENT,
                "Achievement Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for unlocked achievements"
                enableVibration(true)
                enableLights(true)
            }

            notificationManager.createNotificationChannel(dailyChannel)
            notificationManager.createNotificationChannel(streakChannel)
            notificationManager.createNotificationChannel(achievementChannel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showDailyQuizReminder() {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DAILY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time for your daily quiz! 📚")
            .setContentText("Keep your streak going and learn something new today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Challenge yourself with a new quiz. Maintain your streak and unlock achievements!"))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DAILY, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showStreakAlert(currentStreak: Int, isAboutToBreak: Boolean = false) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (title, text) = if (isAboutToBreak) {
            "Don't break your streak! 🔥" to "You're on a $currentStreak day streak. Take a quiz today to keep it going!"
        } else {
            "Amazing streak! 🔥" to "You've maintained your quiz streak for $currentStreak days in a row!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_STREAK)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_STREAK, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showAchievementUnlocked(achievementTitle: String, achievementDescription: String) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_stats", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACHIEVEMENT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🏆 Achievement Unlocked!")
            .setContentText(achievementTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$achievementTitle\n\n$achievementDescription"))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ACHIEVEMENT, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNewPersonalBest(category: String, score: Int) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACHIEVEMENT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🌟 New Personal Best!")
            .setContentText("You scored $score in $category - Your best yet!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ACHIEVEMENT + 1, notification)
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}

