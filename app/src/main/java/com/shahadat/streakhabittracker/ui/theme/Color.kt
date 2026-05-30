package com.shahadat.streakhabittracker.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Dark glassmorphism color palette for Streak HabitTracker.
 * All colors are designed for dark mode with glass-like transparency effects.
 */
object AppColors {
    // Core backgrounds
    val Background = Color(0xFF080810)
    val BackgroundSecondary = Color(0xFF0F0F1A)
    val Surface = Color(0xFF12121F)
    val SurfaceElevated = Color(0xFF1A1A2E)

    // Glass effects
    val SurfaceGlass = Color(0x14FFFFFF)       // 8% white
    val SurfaceGlassHover = Color(0x1FFFFFFF)  // 12% white
    val BorderGlass = Color(0x28FFFFFF)        // 16% white
    val BorderGlassLight = Color(0x40FFFFFF)   // 25% white

    // Accent colors
    val AccentPurple = Color(0xFF8B7CF6)
    val AccentPurpleLight = Color(0xFFADA0FF)
    val AccentPurpleDark = Color(0xFF6C5CE7)
    val AccentBlue = Color(0xFF60A5FA)
    val AccentBlueDark = Color(0xFF3B82F6)
    val AccentGreen = Color(0xFF34D399)        // streaks / completions
    val AccentGreenDark = Color(0xFF10B981)
    val AccentAmber = Color(0xFFFBBF24)        // warnings
    val AccentRed = Color(0xFFEF4444)          // errors / delete
    val AccentPink = Color(0xFFF472B6)
    val AccentTeal = Color(0xFF2DD4BF)
    val AccentOrange = Color(0xFFFB923C)

    // Text colors
    val TextPrimary = Color(0xFFF1F1F6)
    val TextSecondary = Color(0x99F1F1F6)      // 60% white
    val TextTertiary = Color(0x66F1F1F6)       // 40% white
    val TextOnAccent = Color(0xFF080810)

    // Status colors
    val StatusSuccess = Color(0xFF34D399)
    val StatusError = Color(0xFFEF4444)
    val StatusWarning = Color(0xFFFBBF24)

    // Group default colors (for habit groups)
    val GroupColors = listOf(
        Color(0xFF8B7CF6), // Purple
        Color(0xFF60A5FA), // Blue
        Color(0xFF34D399), // Green
        Color(0xFFFB923C), // Orange
        Color(0xFFF472B6), // Pink
        Color(0xFF2DD4BF), // Teal
        Color(0xFFFBBF24), // Amber
        Color(0xFFEF4444), // Red
    )
}

// Extension to convert hex string to Color
fun String.toComposeColor(): Color {
    val hex = this.removePrefix("#")
    return Color(android.graphics.Color.parseColor("#$hex"))
}
