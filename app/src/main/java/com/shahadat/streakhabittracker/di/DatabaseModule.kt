package com.shahadat.streakhabittracker.di

import android.content.Context
import androidx.room.Room
import com.shahadat.streakhabittracker.data.db.AppDatabase
import com.shahadat.streakhabittracker.data.db.dao.HabitDao
import com.shahadat.streakhabittracker.data.db.dao.HabitGroupDao
import com.shahadat.streakhabittracker.data.db.dao.HabitLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing the Room database and all DAOs as singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitGroupDao(db: AppDatabase): HabitGroupDao = db.habitGroupDao()

    @Provides
    @Singleton
    fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()

    @Provides
    @Singleton
    fun provideHabitLogDao(db: AppDatabase): HabitLogDao = db.habitLogDao()
}
