package com.shahadat.streakhabittracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shahadat.streakhabittracker.data.db.dao.HabitDao
import com.shahadat.streakhabittracker.data.db.dao.HabitGroupDao
import com.shahadat.streakhabittracker.data.db.dao.HabitLogDao
import com.shahadat.streakhabittracker.data.db.entities.Habit
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import com.shahadat.streakhabittracker.data.db.entities.HabitLog

/**
 * Main Room database for the Streak HabitTracker app.
 * All habit data is stored locally — zero data leaves the device.
 */
@Database(
    entities = [
        HabitGroup::class,
        Habit::class,
        HabitLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitGroupDao(): HabitGroupDao
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao

    companion object {
        const val DATABASE_NAME = "streak_habit_tracker.db"
    }
}
