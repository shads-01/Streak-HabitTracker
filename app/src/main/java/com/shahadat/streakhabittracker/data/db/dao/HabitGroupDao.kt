package com.shahadat.streakhabittracker.data.db.dao

import androidx.room.*
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for HabitGroup operations.
 */
@Dao
interface HabitGroupDao {

    @Query("SELECT * FROM habit_groups ORDER BY sortOrder ASC")
    fun getAllGroups(): Flow<List<HabitGroup>>

    @Query("SELECT * FROM habit_groups WHERE isLocked = 0 ORDER BY sortOrder ASC")
    fun getUnlockedGroups(): Flow<List<HabitGroup>>

    @Query("SELECT * FROM habit_groups WHERE isLocked = 1 ORDER BY sortOrder ASC")
    fun getLockedGroups(): Flow<List<HabitGroup>>

    @Query("SELECT * FROM habit_groups WHERE id = :id")
    suspend fun getGroupById(id: Long): HabitGroup?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: HabitGroup): Long

    @Update
    suspend fun updateGroup(group: HabitGroup)

    @Delete
    suspend fun deleteGroup(group: HabitGroup)

    @Query("SELECT COALESCE(MAX(sortOrder), 0) + 1 FROM habit_groups")
    suspend fun getNextSortOrder(): Int
}
