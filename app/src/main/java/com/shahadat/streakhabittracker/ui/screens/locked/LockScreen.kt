package com.shahadat.streakhabittracker.ui.screens.locked

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shahadat.streakhabittracker.ui.components.GlassCard
import com.shahadat.streakhabittracker.ui.components.HabitCard
import com.shahadat.streakhabittracker.ui.theme.AppColors

/**
 * Lock screen with PIN entry and number pad.
 * Supports both initial setup and verification modes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LockViewModel = hiltViewModel()
) {
    val state by viewModel.lockState.collectAsStateWithLifecycle()

    // Navigate when unlocked
    LaunchedEffect(state.isUnlocked) {
        if (state.isUnlocked) onUnlocked()
    }

    // Shake animation
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(state.shakeAnimation) {
        if (state.shakeAnimation) {
            repeat(4) {
                shakeOffset.animateTo(10f, tween(50))
                shakeOffset.animateTo(-10f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
            viewModel.clearShakeAnimation()
        }
    }

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Lock icon and title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AppColors.AccentPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = AppColors.AccentPurple,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when {
                        state.isSettingUp && state.setupStep == SetupStep.ENTER -> "Set a PIN"
                        state.isSettingUp && state.setupStep == SetupStep.CONFIRM -> "Confirm PIN"
                        else -> "Enter PIN"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppColors.TextPrimary
                )

                Text(
                    text = when {
                        state.isSettingUp && state.setupStep == SetupStep.ENTER -> "Choose a 4-6 digit PIN for your vault"
                        state.isSettingUp && state.setupStep == SetupStep.CONFIRM -> "Enter the same PIN again"
                        else -> "Unlock your private habits"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // PIN dots
            val currentPin = when {
                state.isSettingUp && state.setupStep == SetupStep.ENTER -> state.setupPin
                state.isSettingUp && state.setupStep == SetupStep.CONFIRM -> state.confirmPin
                else -> state.enteredPin
            }

            Row(
                modifier = Modifier.graphicsLayer { translationX = shakeOffset.value },
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < currentPin.length) AppColors.AccentPurple
                                else Color.White.copy(alpha = 0.15f)
                            )
                    )
                }
            }

            // Error message
            AnimatedVisibility(visible = state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.AccentRed,
                    textAlign = TextAlign.Center
                )
            }

            // Number pad
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "⌫")
                )

                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(modifier = Modifier.size(72.dp))
                            } else if (key == "⌫") {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .clickable { viewModel.onPinBackspace() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Delete",
                                        tint = AppColors.TextSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .clickable {
                                            if (state.wrongAttempts < 5) {
                                                viewModel.onPinDigitEntered(key)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = AppColors.TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Locked vault screen showing hidden habits with their own streaks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockedVaultScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHabitCalendar: (Long) -> Unit,
    viewModel: LockViewModel = hiltViewModel()
) {
    val state by viewModel.vaultState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadLockedHabits()
    }

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = AppColors.AccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Locked Vault",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AppColors.TextPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColors.AccentPurple)
            }
        } else if (state.habits.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔒", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No locked habits yet.\nCreate a locked group and add habits to it.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.habits, key = { it.id }) { habit ->
                    HabitCard(
                        state = habit,
                        onComplete = { viewModel.completeLockedHabit(habit.id) },
                        onUncomplete = { viewModel.uncompleteLockedHabit(habit.id) },
                        onEdit = { /* Edit not available from vault for security */ },
                        onDelete = { /* Delete not available from vault */ },
                        onViewCalendar = { onNavigateToHabitCalendar(habit.id) }
                    )
                }
            }
        }
    }
}
