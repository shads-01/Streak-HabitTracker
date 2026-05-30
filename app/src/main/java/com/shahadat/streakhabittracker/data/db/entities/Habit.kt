package com.shahadat.streakhabittracker.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single habit tracked by the user.
 * Each habit belongs to a group and can optionally have a daily reminder.
 */
@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = HabitGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId")]
)
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val name: String,
    val reminderTime: String? = null,    // "HH:mm" format, e.g. "05:30"
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
