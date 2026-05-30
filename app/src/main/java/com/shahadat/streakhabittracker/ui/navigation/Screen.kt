package com.shahadat.streakhabittracker.ui.navigation

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddHabit : Screen("add_habit")
    data object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Long) = "edit_habit/$habitId"
    }
    data object Calendar : Screen("calendar")
    data object HabitCalendar : Screen("habit_calendar/{habitId}") {
        fun createRoute(habitId: Long) = "habit_calendar/$habitId"
    }
    data object Lock : Screen("lock")
    data object LockedVault : Screen("locked_vault")
    data object Settings : Screen("settings")
}
