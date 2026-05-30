package com.shahadat.streakhabittracker.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shahadat.streakhabittracker.ui.components.GroupFilterRow
import com.shahadat.streakhabittracker.ui.components.HabitCard
import com.shahadat.streakhabittracker.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddHabit: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLock: () -> Unit,
    onNavigateToHabitCalendar: (Long) -> Unit,
    onNavigateToEditHabit: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }

    // Show undo snackbar when a habit is completed
    LaunchedEffect(uiState.lastCompletedHabitId) {
        uiState.lastCompletedHabitId?.let { habitId ->
            val result = snackbarHostState.showSnackbar(
                message = "Habit completed! 🎉",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoLastCompletion()
            } else {
                viewModel.clearLastCompleted()
            }
        }
    }

    Scaffold(
        containerColor = AppColors.Background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = AppColors.SurfaceElevated,
                    contentColor = AppColors.TextPrimary,
                    actionColor = AppColors.AccentPurple
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Streak",
                        style = MaterialTheme.typography.headlineLarge,
                        color = AppColors.TextPrimary
                    )
                },
                actions = {
                    if (uiState.hasLockedGroups) {
                        IconButton(onClick = onNavigateToLock) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Locked Vault",
                                tint = AppColors.TextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToCalendar) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = AppColors.TextSecondary
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = AppColors.TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddHabit,
                shape = CircleShape,
                containerColor = AppColors.AccentPurple,
                contentColor = AppColors.TextOnAccent
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Group filter pills
            if (uiState.groups.isNotEmpty()) {
                GroupFilterRow(
                    groups = uiState.groups,
                    selectedGroupId = uiState.selectedGroupId,
                    onGroupSelected = { viewModel.selectGroup(it) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Habit list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.AccentPurple)
                }
            } else if (uiState.habits.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🌟",
                            fontSize = 48.sp
                        )
                        Text(
                            text = "No habits yet.\nTap + to add your first habit!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.habits,
                        key = { it.id }
                    ) { habitState ->
                        AnimatedVisibility(
                            visible = true,
                            enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                            exit = shrinkVertically(tween(300)) + fadeOut(tween(300))
                        ) {
                            HabitCard(
                                state = habitState,
                                onComplete = { viewModel.completeHabit(habitState.id) },
                                onUncomplete = { viewModel.uncompleteHabit(habitState.id) },
                                onEdit = { onNavigateToEditHabit(habitState.id) },
                                onDelete = { showDeleteDialog = habitState.id },
                                onViewCalendar = { onNavigateToHabitCalendar(habitState.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { habitId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Habit", color = AppColors.TextPrimary) },
            text = {
                Text(
                    "Are you sure you want to delete this habit? All streak data will be lost.",
                    color = AppColors.TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHabit(habitId)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = AppColors.AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel", color = AppColors.TextSecondary)
                }
            },
            containerColor = AppColors.SurfaceElevated,
        )
    }
}
