package com.shahadat.streakhabittracker.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shahadat.streakhabittracker.data.repository.HabitRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver that reschedules all habit reminders after device reboot.
 * WorkManager persists through reboot, but this provides extra reliability.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: HabitRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Reschedule all habit reminders
                val habitsWithReminders = repository.getHabitsWithReminders()
                for (habit in habitsWithReminders) {
                    habit.reminderTime?.let { time ->
                        HabitReminderWorker.scheduleReminder(
                            context = context,
                            habitId = habit.id,
                            habitName = habit.name,
                            reminderTime = time
                        )
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
