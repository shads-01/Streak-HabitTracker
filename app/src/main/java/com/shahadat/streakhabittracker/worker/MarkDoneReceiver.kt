package com.shahadat.streakhabittracker.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shahadat.streakhabittracker.data.db.entities.HabitLog
import com.shahadat.streakhabittracker.data.repository.HabitRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * BroadcastReceiver that handles "Mark Done" actions from notification bar.
 * Inserts a HabitLog and dismisses the notification.
 */
@AndroidEntryPoint
class MarkDoneReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: HabitRepository

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra("habit_id", -1L)
        val habitName = intent.getStringExtra("habit_name") ?: ""

        if (habitId == -1L) return

        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Use goAsync() for coroutine work in BroadcastReceiver
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if already completed today
                val existingLog = repository.getLogByHabitAndDate(habitId, today)
                if (existingLog == null) {
                    repository.insertLog(
                        HabitLog(
                            habitId = habitId,
                            completedDate = today
                        )
                    )
                }

                // Cancel the notification
                NotificationHelper.cancelNotification(context, habitId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
