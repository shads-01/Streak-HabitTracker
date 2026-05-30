package com.shahadat.streakhabittracker.data.db.dao

import androidx.room.*
import com.shahadat.streakhabittracker.data.db.entities.Habit
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Habit operations.
 */
@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY sortOrder ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE groupId = :groupId ORDER BY sortOrder ASC")
    fun getHabitsByGroup(groupId: Long): Flow<List<Habit>>

    @Query("""
        SELECT h.* FROM habits h 
        INNER JOIN habit_groups g ON h.groupId = g.id 
        WHERE g.isLocked = 0 
        ORDER BY h.sortOrder ASC
    """)
    fun getUnlockedHabits(): Flow<List<Habit>>

    @Query("""
        SELECT h.* FROM habits h 
        INNER JOIN habit_groups g ON h.groupId = g.id 
        WHERE g.isLocked = 1 
        ORDER BY h.sortOrder ASC
    """)
    fun getLockedHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): Habit?

    @Query("SELECT * FROM habits WHERE reminderEnabled = 1")
    suspend fun getHabitsWithReminders(): List<Habit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: Long)

    @Query("SELECT COALESCE(MAX(sortOrder), 0) + 1 FROM habits WHERE groupId = :groupId")
    suspend fun getNextSortOrder(groupId: Long): Int
}
