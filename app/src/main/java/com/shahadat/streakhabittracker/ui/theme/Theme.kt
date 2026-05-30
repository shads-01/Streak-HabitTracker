package com.shahadat.streakhabittracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.AccentPurple,
    onPrimary = AppColors.TextOnAccent,
    primaryContainer = AppColors.AccentPurpleDark,
    onPrimaryContainer = AppColors.TextPrimary,
    secondary = AppColors.AccentBlue,
    onSecondary = AppColors.TextOnAccent,
    secondaryContainer = AppColors.AccentBlueDark,
    onSecondaryContainer = AppColors.TextPrimary,
    tertiary = AppColors.AccentGreen,
    onTertiary = AppColors.TextOnAccent,
    tertiaryContainer = AppColors.AccentGreenDark,
    onTertiaryContainer = AppColors.TextPrimary,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.SurfaceElevated,
    onSurfaceVariant = AppColors.TextSecondary,
    error = AppColors.StatusError,
    onError = AppColors.TextPrimary,
    outline = AppColors.BorderGlass,
    outlineVariant = AppColors.BorderGlassLight,
)

@Composable
fun StreakHabitTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
