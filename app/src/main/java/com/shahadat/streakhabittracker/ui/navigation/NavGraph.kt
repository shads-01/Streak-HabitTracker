package com.shahadat.streakhabittracker.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shahadat.streakhabittracker.ui.screens.addhabit.AddHabitScreen
import com.shahadat.streakhabittracker.ui.screens.calendar.CalendarScreen
import com.shahadat.streakhabittracker.ui.screens.calendar.HabitCalendarScreen
import com.shahadat.streakhabittracker.ui.screens.home.HomeScreen
import com.shahadat.streakhabittracker.ui.screens.locked.LockScreen
import com.shahadat.streakhabittracker.ui.screens.locked.LockedVaultScreen
import com.shahadat.streakhabittracker.ui.screens.settings.SettingsScreen

/**
 * Main navigation graph for the app.
 * Uses fade + slide animations for screen transitions.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(300)
                )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) +
                slideOutVertically(
                    targetOffsetY = { 40 },
                    animationSpec = tween(200)
                )
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddHabit = { navController.navigate(Screen.AddHabit.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToLock = { navController.navigate(Screen.Lock.route) },
                onNavigateToHabitCalendar = { habitId ->
                    navController.navigate(Screen.HabitCalendar.createRoute(habitId))
                },
                onNavigateToEditHabit = { habitId ->
                    navController.navigate(Screen.EditHabit.createRoute(habitId))
                }
            )
        }

        composable(Screen.AddHabit.route) {
            AddHabitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditHabit.route,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            AddHabitScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHabitCalendar = { habitId ->
                    navController.navigate(Screen.HabitCalendar.createRoute(habitId))
                }
            )
        }

        composable(
            route = Screen.HabitCalendar.route,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            HabitCalendarScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Lock.route) {
            LockScreen(
                onUnlocked = {
                    navController.navigate(Screen.LockedVault.route) {
                        popUpTo(Screen.Lock.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LockedVault.route) {
            LockedVaultScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHabitCalendar = { habitId ->
                    navController.navigate(Screen.HabitCalendar.createRoute(habitId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
