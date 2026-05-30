package com.shahadat.streakhabittracker.ui.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shahadat.streakhabittracker.ui.theme.AppColors

/**
 * Calendar overview screen. Fully implemented in feature/calendar branch.
 */
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHabitCalendar: (Long) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background
    ) {
        Text("Calendar Screen - Coming Soon", color = AppColors.TextPrimary)
    }
}

/**
 * Per-habit calendar detail screen.
 */
@Composable
fun HabitCalendarScreen(
    habitId: Long,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background
    ) {
        Text("Habit Calendar Screen - Coming Soon", color = AppColors.TextPrimary)
    }
}
