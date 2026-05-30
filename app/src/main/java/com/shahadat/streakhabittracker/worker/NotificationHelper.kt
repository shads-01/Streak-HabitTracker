package com.shahadat.streakhabittracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shahadat.streakhabittracker.MainActivity
import com.shahadat.streakhabittracker.R

/**
 * Helper object for creating and managing habit reminder notifications.
 */
object NotificationHelper {

    const val CHANNEL_ID = "habit_reminders"
    const val CHANNEL_NAME = "Habit Reminders"
    const val CHANNEL_DESC = "Notifications for daily habit reminders"

    /**
     * Creates the notification channel (required for Android 8.0+).
     * Safe to call multiple times — it's a no-op if already created.
     */
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESC
            enableVibration(true)
            enableLights(true)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Shows a habit reminder notification with a "Mark Done" action button.
     */
    fun showReminderNotification(
        context: Context,
        habitId: Long,
        habitName: String
    ) {
        createNotificationChannel(context)

        // Intent to open the app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mark Done action
        val markDoneIntent = Intent(context, MarkDoneReceiver::class.java).apply {
            putExtra("habit_id", habitId)
            putExtra("habit_name", habitName)
        }
        val markDonePendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt() + 10000, // offset to avoid conflict with content intent
            markDoneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Time for $habitName")
            .setContentText("Keep your streak going! 🔥")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_send,
                "Mark Done ✓",
                markDonePendingIntent
            )
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(habitId.toInt(), notification)
    }

    /**
     * Cancels a notification for a specific habit.
     */
    fun cancelNotification(context: Context, habitId: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(habitId.toInt())
    }
}
