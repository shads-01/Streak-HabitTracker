package com.shahadat.streakhabittracker.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shahadat.streakhabittracker.ui.theme.AppColors

/**
 * Settings screen. Fully implemented in feature/settings branch.
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background
    ) {
        Text("Settings Screen - Coming Soon", color = AppColors.TextPrimary)
    }
}
