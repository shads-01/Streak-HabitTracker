package com.shahadat.streakhabittracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a habit group (e.g., "Prayer", "Skills", "Health").
 * Groups allow users to organize and filter their habits.
 * A group can be marked as locked to require vault authentication.
 */
@Entity(tableName = "habit_groups")
data class HabitGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val isLocked: Boolean = false,
    val sortOrder: Int = 0
)
