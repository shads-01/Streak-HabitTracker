package com.shahadat.streakhabittracker.ui.screens.addhabit

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shahadat.streakhabittracker.ui.components.GlassCard
import com.shahadat.streakhabittracker.ui.theme.AppColors
import com.shahadat.streakhabittracker.ui.theme.toComposeColor

/**
 * Add or edit habit screen with group selection, reminder time picker,
 * and inline group creation dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    habitId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Load habit for editing if habitId is provided
    LaunchedEffect(habitId) {
        habitId?.let { viewModel.loadHabitForEditing(it) }
    }

    // Navigate back on successful save
    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditing) "Edit Habit" else "Add Habit",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AppColors.TextPrimary
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Error message
            uiState.errorMessage?.let { error ->
                GlassCard(backgroundAlpha = 0.05f) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = AppColors.AccentRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = error, color = AppColors.AccentRed, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Habit Name
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Habit Name",
                        style = MaterialTheme.typography.labelLarge,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.habitName,
                        onValueChange = { viewModel.updateHabitName(it) },
                        placeholder = {
                            Text("e.g., Read 30 minutes", color = AppColors.TextTertiary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AppColors.TextPrimary,
                            unfocusedTextColor = AppColors.TextPrimary,
                            cursorColor = AppColors.AccentPurple,
                            focusedBorderColor = AppColors.AccentPurple,
                            unfocusedBorderColor = AppColors.BorderGlass
                        ),
                        singleLine = true
                    )
                }
            }

            // Group Selection
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Group",
                            style = MaterialTheme.typography.labelLarge,
                            color = AppColors.TextSecondary
                        )
                        TextButton(onClick = { viewModel.toggleGroupDialog(true) }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Group", color = AppColors.AccentPurple)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.groups.isEmpty()) {
                        Text(
                            text = "No groups yet. Create one first!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextTertiary
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(uiState.groups) { _, group ->
                                val isSelected = uiState.selectedGroupId == group.id
                                val groupColor = group.colorHex.toComposeColor()
                                val shape = RoundedCornerShape(12.dp)

                                Box(
                                    modifier = Modifier
                                        .clip(shape)
                                        .background(
                                            if (isSelected) groupColor.copy(alpha = 0.2f)
                                            else Color.Transparent
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) groupColor else AppColors.BorderGlass,
                                            shape
                                        )
                                        .clickable { viewModel.selectGroup(group.id) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(groupColor)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = group.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) groupColor else AppColors.TextSecondary
                                        )
                                        if (group.isLocked) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.Lock,
                                                contentDescription = "Locked",
                                                tint = AppColors.TextTertiary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Reminder
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Reminder",
                                style = MaterialTheme.typography.labelLarge,
                                color = AppColors.TextSecondary
                            )
                            if (uiState.reminderEnabled && uiState.reminderTime != null) {
                                Text(
                                    text = uiState.reminderTime!!,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = AppColors.AccentPurple
                                )
                            }
                        }
                        Switch(
                            checked = uiState.reminderEnabled,
                            onCheckedChange = { viewModel.toggleReminder(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AppColors.AccentPurple,
                                checkedTrackColor = AppColors.AccentPurple.copy(alpha = 0.3f),
                                uncheckedThumbColor = AppColors.TextTertiary,
                                uncheckedTrackColor = AppColors.SurfaceGlass
                            )
                        )
                    }

                    if (uiState.reminderEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                val currentTime = uiState.reminderTime?.split(":") ?: listOf("9", "0")
                                val hour = currentTime[0].toIntOrNull() ?: 9
                                val minute = currentTime.getOrNull(1)?.toIntOrNull() ?: 0

                                TimePickerDialog(
                                    context,
                                    { _, h, m ->
                                        viewModel.updateReminderTime(
                                            String.format("%02d:%02d", h, m)
                                        )
                                    },
                                    hour,
                                    minute,
                                    true
                                ).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AppColors.AccentPurple
                            )
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (uiState.reminderTime != null) "Change Time" else "Set Time")
                        }
                    }
                }
            }

            // Save Button
            Button(
                onClick = { viewModel.saveHabit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.AccentPurple,
                    contentColor = AppColors.TextOnAccent
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = AppColors.TextOnAccent
                    )
                } else {
                    Text(
                        text = if (uiState.isEditing) "Update Habit" else "Create Habit",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    // Create Group Dialog
    if (uiState.showGroupDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleGroupDialog(false) },
            title = {
                Text("Create Group", color = AppColors.TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.newGroupName,
                        onValueChange = { viewModel.updateNewGroupName(it) },
                        label = { Text("Group Name", color = AppColors.TextTertiary) },
                        placeholder = { Text("e.g., Health", color = AppColors.TextTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AppColors.TextPrimary,
                            unfocusedTextColor = AppColors.TextPrimary,
                            cursorColor = AppColors.AccentPurple,
                            focusedBorderColor = AppColors.AccentPurple,
                            unfocusedBorderColor = AppColors.BorderGlass
                        ),
                        singleLine = true
                    )

                    Text("Color", style = MaterialTheme.typography.labelMedium, color = AppColors.TextSecondary)

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(AppColors.GroupColors) { index, color ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(
                                        if (uiState.newGroupColorIndex == index) {
                                            Modifier.border(3.dp, Color.White, CircleShape)
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clickable { viewModel.updateNewGroupColorIndex(index) }
                            ) {
                                if (uiState.newGroupColorIndex == index) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = uiState.isLockedGroup,
                            onCheckedChange = { viewModel.toggleLockedGroup(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AppColors.AccentPurple,
                                uncheckedColor = AppColors.TextTertiary
                            )
                        )
                        Column {
                            Text(
                                "Locked Group",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.TextPrimary
                            )
                            Text(
                                "Requires PIN/pattern to view",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextTertiary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.createGroup() },
                    enabled = uiState.newGroupName.isNotBlank()
                ) {
                    Text("Create", color = AppColors.AccentPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleGroupDialog(false) }) {
                    Text("Cancel", color = AppColors.TextSecondary)
                }
            },
            containerColor = AppColors.SurfaceElevated
        )
    }
}
