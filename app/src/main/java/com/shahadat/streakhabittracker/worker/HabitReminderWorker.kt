package com.shahadat.streakhabittracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that fires habit reminder notifications.
 * Scheduled for each habit's reminder time; reschedules itself for the next day.
 */
@HiltWorker
class HabitReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val habitId = inputData.getLong(KEY_HABIT_ID, -1L)
        val habitName = inputData.getString(KEY_HABIT_NAME) ?: "Your habit"
        val reminderTime = inputData.getString(KEY_REMINDER_TIME)

        if (habitId == -1L) return Result.failure()

        // Show the notification
        NotificationHelper.showReminderNotification(context, habitId, habitName)

        // Reschedule for next day
        if (reminderTime != null) {
            scheduleReminder(context, habitId, habitName, reminderTime)
        }

        return Result.success()
    }

    companion object {
        const val KEY_HABIT_ID = "habit_id"
        const val KEY_HABIT_NAME = "habit_name"
        const val KEY_REMINDER_TIME = "reminder_time"

        /**
         * Schedules a one-time reminder for the given habit.
         * If the reminder time has already passed today, schedules for tomorrow.
         */
        fun scheduleReminder(
            context: Context,
            habitId: Long,
            habitName: String,
            reminderTime: String // "HH:mm"
        ) {
            val parts = reminderTime.split(":")
            if (parts.size != 2) return

            val hour = parts[0].toIntOrNull() ?: return
            val minute = parts[1].toIntOrNull() ?: return

            val now = LocalDateTime.now()
            var targetTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute))

            // If the time has passed today, schedule for tomorrow
            if (targetTime.isBefore(now)) {
                targetTime = targetTime.plusDays(1)
            }

            val delay = Duration.between(now, targetTime)

            val inputData = Data.Builder()
                .putLong(KEY_HABIT_ID, habitId)
                .putString(KEY_HABIT_NAME, habitName)
                .putString(KEY_REMINDER_TIME, reminderTime)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
                .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("habit_reminder_$habitId")
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "habit_reminder_$habitId",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }

        /**
         * Cancels a scheduled reminder for a habit.
         */
        fun cancelReminder(context: Context, habitId: Long) {
            WorkManager.getInstance(context)
                .cancelUniqueWork("habit_reminder_$habitId")
        }

        /**
         * Cancels all scheduled reminders.
         */
        fun cancelAllReminders(context: Context) {
            WorkManager.getInstance(context)
                .cancelAllWorkByTag("habit_reminder")
        }
    }
}
