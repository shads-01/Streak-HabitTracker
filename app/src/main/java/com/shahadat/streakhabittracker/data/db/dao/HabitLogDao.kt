package com.shahadat.streakhabittracker.data.db.dao

import androidx.room.*
import com.shahadat.streakhabittracker.data.db.entities.HabitLog
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for HabitLog operations.
 * Used for tracking completions and calculating streaks.
 */
@Dao
interface HabitLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLog): Long

    @Delete
    suspend fun deleteLog(log: HabitLog)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND completedDate = :date")
    suspend fun deleteLogByHabitAndDate(habitId: Long, date: String)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND completedDate = :date LIMIT 1")
    suspend fun getLogByHabitAndDate(habitId: Long, date: String): HabitLog?

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY completedDate DESC")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY completedDate DESC")
    suspend fun getLogsForHabitSync(habitId: Long): List<HabitLog>

    @Query("SELECT * FROM habit_logs WHERE completedDate = :date")
    fun getLogsForDate(date: String): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE completedDate = :date")
    suspend fun getLogsForDateSync(date: String): List<HabitLog>

    @Query("""
        SELECT completedDate FROM habit_logs 
        WHERE habitId = :habitId 
        ORDER BY completedDate DESC
    """)
    suspend fun getCompletedDatesForHabit(habitId: Long): List<String>

    @Query("""
        SELECT completedDate FROM habit_logs 
        WHERE habitId = :habitId 
        AND completedDate BETWEEN :startDate AND :endDate 
        ORDER BY completedDate ASC
    """)
    suspend fun getCompletedDatesInRange(habitId: Long, startDate: String, endDate: String): List<String>

    @Query("SELECT COUNT(*) FROM habit_logs WHERE habitId = :habitId")
    suspend fun getTotalCompletionsForHabit(habitId: Long): Int

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
    suspend fun deleteAllLogsForHabit(habitId: Long)

    @Query("SELECT * FROM habit_logs")
    suspend fun getAllLogs(): List<HabitLog>
}
