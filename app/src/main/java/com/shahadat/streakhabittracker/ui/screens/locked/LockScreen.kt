package com.shahadat.streakhabittracker.ui.screens.locked

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shahadat.streakhabittracker.ui.theme.AppColors

/**
 * Lock screen for vault authentication. Fully implemented in feature/locked-vault branch.
 */
@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background
    ) {
        Text("Lock Screen - Coming Soon", color = AppColors.TextPrimary)
    }
}

/**
 * Locked vault habit list. Fully implemented in feature/locked-vault branch.
 */
@Composable
fun LockedVaultScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHabitCalendar: (Long) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background
    ) {
        Text("Locked Vault Screen - Coming Soon", color = AppColors.TextPrimary)
    }
}
