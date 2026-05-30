package com.shahadat.streakhabittracker.di

import com.shahadat.streakhabittracker.data.repository.HabitRepository
import com.shahadat.streakhabittracker.data.repository.HabitRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding repository implementations to their interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository
}
