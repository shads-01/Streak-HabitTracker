package com.shahadat.streakhabittracker.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single completion event for a habit.
 * One row per completion per day. Used for streak calculation.
 * 
 * Streak calculation: count consecutive days backward from today
 * where a HabitLog exists for a given habitId.
 */
@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("habitId"),
        Index(value = ["habitId", "completedDate"], unique = true)
    ]
)
data class HabitLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val completedDate: String,    // ISO date string: "2025-05-30"
    val completedAt: Long = System.currentTimeMillis()
)
