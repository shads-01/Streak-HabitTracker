package com.shahadat.streakhabittracker.data.repository

import com.shahadat.streakhabittracker.data.db.dao.HabitDao
import com.shahadat.streakhabittracker.data.db.dao.HabitGroupDao
import com.shahadat.streakhabittracker.data.db.dao.HabitLogDao
import com.shahadat.streakhabittracker.data.db.entities.Habit
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import com.shahadat.streakhabittracker.data.db.entities.HabitLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Implementation of [HabitRepository].
 * Delegates to Room DAOs and provides streak calculation logic.
 */
class HabitRepositoryImpl @Inject constructor(
    private val habitGroupDao: HabitGroupDao,
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao
) : HabitRepository {

    // --- Groups ---
    override fun getAllGroups(): Flow<List<HabitGroup>> = habitGroupDao.getAllGroups()
    override fun getUnlockedGroups(): Flow<List<HabitGroup>> = habitGroupDao.getUnlockedGroups()
    override fun getLockedGroups(): Flow<List<HabitGroup>> = habitGroupDao.getLockedGroups()
    override suspend fun getGroupById(id: Long): HabitGroup? = habitGroupDao.getGroupById(id)

    override suspend fun insertGroup(group: HabitGroup): Long {
        val sortOrder = habitGroupDao.getNextSortOrder()
        return habitGroupDao.insertGroup(group.copy(sortOrder = sortOrder))
    }

    override suspend fun updateGroup(group: HabitGroup) = habitGroupDao.updateGroup(group)
    override suspend fun deleteGroup(group: HabitGroup) = habitGroupDao.deleteGroup(group)

    // --- Habits ---
    override fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()
    override fun getUnlockedHabits(): Flow<List<Habit>> = habitDao.getUnlockedHabits()
    override fun getLockedHabits(): Flow<List<Habit>> = habitDao.getLockedHabits()
    override fun getHabitsByGroup(groupId: Long): Flow<List<Habit>> = habitDao.getHabitsByGroup(groupId)
    override suspend fun getHabitById(id: Long): Habit? = habitDao.getHabitById(id)

    override suspend fun insertHabit(habit: Habit): Long {
        val sortOrder = habitDao.getNextSortOrder(habit.groupId)
        return habitDao.insertHabit(habit.copy(sortOrder = sortOrder))
    }

    override suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    override suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)
    override suspend fun deleteHabitById(habitId: Long) = habitDao.deleteHabitById(habitId)
    override suspend fun getHabitsWithReminders(): List<Habit> = habitDao.getHabitsWithReminders()

    // --- Logs ---
    override suspend fun insertLog(log: HabitLog): Long = habitLogDao.insertLog(log)
    override suspend fun deleteLog(log: HabitLog) = habitLogDao.deleteLog(log)
    override suspend fun deleteLogByHabitAndDate(habitId: Long, date: String) =
        habitLogDao.deleteLogByHabitAndDate(habitId, date)

    override suspend fun getLogByHabitAndDate(habitId: Long, date: String): HabitLog? =
        habitLogDao.getLogByHabitAndDate(habitId, date)

    override fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>> =
        habitLogDao.getLogsForHabit(habitId)

    override fun getLogsForDate(date: String): Flow<List<HabitLog>> =
        habitLogDao.getLogsForDate(date)

    override suspend fun getLogsForDateSync(date: String): List<HabitLog> =
        habitLogDao.getLogsForDateSync(date)

    override suspend fun getCompletedDatesForHabit(habitId: Long): List<String> =
        habitLogDao.getCompletedDatesForHabit(habitId)

    override suspend fun getCompletedDatesInRange(
        habitId: Long,
        startDate: String,
        endDate: String
    ): List<String> = habitLogDao.getCompletedDatesInRange(habitId, startDate, endDate)

    override suspend fun getTotalCompletionsForHabit(habitId: Long): Int =
        habitLogDao.getTotalCompletionsForHabit(habitId)

    // --- Streak Calculation ---
    /**
     * Calculate the current streak for a habit.
     * Counts consecutive days backward from today (or yesterday if today isn't done yet)
     * where a completion log exists.
     */
    override suspend fun calculateStreak(habitId: Long): Int {
        val completedDates = habitLogDao.getCompletedDatesForHabit(habitId)
        if (completedDates.isEmpty()) return 0

        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val completedDateSet = completedDates.map { LocalDate.parse(it, dateFormatter) }.toSet()

        val today = LocalDate.now()
        var streak = 0

        // Start from today. If today is completed, include it.
        // If not, start from yesterday.
        var checkDate = if (completedDateSet.contains(today)) {
            today
        } else {
            today.minusDays(1)
        }

        while (completedDateSet.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }

    // --- Data Management ---
    override suspend fun getAllLogs(): List<HabitLog> = habitLogDao.getAllLogs()

    override suspend fun getAllHabitsSync(): List<Habit> =
        habitDao.getAllHabits().first()

    override suspend fun getAllGroupsSync(): List<HabitGroup> =
        habitGroupDao.getAllGroups().first()
}
