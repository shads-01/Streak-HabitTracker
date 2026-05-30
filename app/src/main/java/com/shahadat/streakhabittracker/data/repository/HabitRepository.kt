package com.shahadat.streakhabittracker.data.repository

import com.shahadat.streakhabittracker.data.db.entities.Habit
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import com.shahadat.streakhabittracker.data.db.entities.HabitLog
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for all habit-related data operations.
 * Abstracts the data layer from ViewModels.
 */
interface HabitRepository {

    // --- Groups ---
    fun getAllGroups(): Flow<List<HabitGroup>>
    fun getUnlockedGroups(): Flow<List<HabitGroup>>
    fun getLockedGroups(): Flow<List<HabitGroup>>
    suspend fun getGroupById(id: Long): HabitGroup?
    suspend fun insertGroup(group: HabitGroup): Long
    suspend fun updateGroup(group: HabitGroup)
    suspend fun deleteGroup(group: HabitGroup)

    // --- Habits ---
    fun getAllHabits(): Flow<List<Habit>>
    fun getUnlockedHabits(): Flow<List<Habit>>
    fun getLockedHabits(): Flow<List<Habit>>
    fun getHabitsByGroup(groupId: Long): Flow<List<Habit>>
    suspend fun getHabitById(id: Long): Habit?
    suspend fun insertHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun deleteHabitById(habitId: Long)
    suspend fun getHabitsWithReminders(): List<Habit>

    // --- Logs ---
    suspend fun insertLog(log: HabitLog): Long
    suspend fun deleteLog(log: HabitLog)
    suspend fun deleteLogByHabitAndDate(habitId: Long, date: String)
    suspend fun getLogByHabitAndDate(habitId: Long, date: String): HabitLog?
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>>
    fun getLogsForDate(date: String): Flow<List<HabitLog>>
    suspend fun getLogsForDateSync(date: String): List<HabitLog>
    suspend fun getCompletedDatesForHabit(habitId: Long): List<String>
    suspend fun getCompletedDatesInRange(habitId: Long, startDate: String, endDate: String): List<String>
    suspend fun getTotalCompletionsForHabit(habitId: Long): Int

    // --- Streak Calculation ---
    suspend fun calculateStreak(habitId: Long): Int

    // --- Data Management ---
    suspend fun getAllLogs(): List<HabitLog>
    suspend fun getAllHabitsSync(): List<Habit>
    suspend fun getAllGroupsSync(): List<HabitGroup>
}
