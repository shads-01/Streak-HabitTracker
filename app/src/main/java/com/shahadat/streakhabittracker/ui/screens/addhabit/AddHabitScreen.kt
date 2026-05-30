package com.shahadat.streakhabittracker.ui.screens.addhabit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shahadat.streakhabittracker.ui.theme.AppColors

/**
 * Add/Edit habit screen. Fully implemented in feature/add-edit-habit branch.
 */
@Composable
fun AddHabitScreen(
    habitId: Long? = null,
    onNavigateBack: () -> Unit
) {
    // Placeholder - will be fully implemented in feature/add-edit-habit branch
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background
    ) {
        Text("Add Habit Screen - Coming Soon", color = AppColors.TextPrimary)
    }
}
