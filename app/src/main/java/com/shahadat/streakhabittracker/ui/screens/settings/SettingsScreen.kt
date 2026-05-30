package com.shahadat.streakhabittracker.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shahadat.streakhabittracker.ui.components.GlassCard
import com.shahadat.streakhabittracker.ui.theme.AppColors

/**
 * Settings screen with all app configuration options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Wallpaper picker
    val wallpaperLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setWallpaper(it) }
    }

    // Import file picker
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importData(it) }
    }

    // Show snackbar for success/error
    LaunchedEffect(uiState.showExportSuccess) {
        if (uiState.showExportSuccess) {
            snackbarHostState.showSnackbar("Data exported successfully")
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.showImportSuccess) {
        if (uiState.showImportSuccess) {
            snackbarHostState.showSnackbar("Data imported successfully")
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = AppColors.Background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = AppColors.SurfaceElevated,
                    contentColor = AppColors.TextPrimary
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- APPEARANCE ---
            SettingsSectionHeader(title = "Appearance", icon = Icons.Default.Palette)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(4.dp)) {
                    SettingsItem(
                        title = "Choose Wallpaper",
                        subtitle = "Select an image for the background",
                        icon = Icons.Default.Image,
                        onClick = { wallpaperLauncher.launch("image/*") }
                    )

                    if (uiState.hasWallpaper) {
                        SettingsItem(
                            title = "Remove Wallpaper",
                            subtitle = "Return to default background",
                            icon = Icons.Default.HideImage,
                            onClick = { viewModel.removeWallpaper() }
                        )
                    }
                }
            }

            // --- NOTIFICATIONS ---
            SettingsSectionHeader(title = "Notifications", icon = Icons.Default.Notifications)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Enable Notifications",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            "Receive reminders for your habits",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AppColors.AccentPurple,
                            checkedTrackColor = AppColors.AccentPurple.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            // --- VAULT LOCK ---
            SettingsSectionHeader(title = "Vault Lock", icon = Icons.Default.Lock)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(4.dp)) {
                    if (uiState.isVaultSetup) {
                        // Auto-lock setting
                        var showAutoLockMenu by remember { mutableStateOf(false) }
                        Box {
                            SettingsItem(
                                title = "Auto-Lock",
                                subtitle = when (uiState.autoLockDuration) {
                                    "immediate" -> "Immediate"
                                    "1min" -> "After 1 minute"
                                    "5min" -> "After 5 minutes"
                                    else -> "Immediate"
                                },
                                icon = Icons.Default.Timer,
                                onClick = { showAutoLockMenu = true }
                            )
                            DropdownMenu(
                                expanded = showAutoLockMenu,
                                onDismissRequest = { showAutoLockMenu = false },
                                containerColor = AppColors.SurfaceElevated
                            ) {
                                listOf("immediate" to "Immediate", "1min" to "After 1 minute", "5min" to "After 5 minutes").forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, color = AppColors.TextPrimary) },
                                        onClick = {
                                            viewModel.setAutoLockDuration(value)
                                            showAutoLockMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        SettingsItem(
                            title = "Reset Vault",
                            subtitle = "Remove PIN and unlock all groups",
                            icon = Icons.Default.LockOpen,
                            titleColor = AppColors.AccentRed,
                            onClick = { viewModel.resetVault() }
                        )
                    } else {
                        SettingsItem(
                            title = "Set Up Vault",
                            subtitle = "Create a PIN to lock private habits",
                            icon = Icons.Default.LockOpen,
                            onClick = { /* Navigate to lock setup - handled via lock screen */ }
                        )
                    }
                }
            }

            // --- DATA ---
            SettingsSectionHeader(title = "Data", icon = Icons.Default.Storage)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(4.dp)) {
                    SettingsItem(
                        title = "Export Data",
                        subtitle = "Save all data as JSON backup",
                        icon = Icons.Default.Upload,
                        onClick = { viewModel.exportData() }
                    )

                    SettingsItem(
                        title = "Import Data",
                        subtitle = "Restore from JSON backup",
                        icon = Icons.Default.Download,
                        onClick = { importLauncher.launch("application/json") }
                    )

                    SettingsItem(
                        title = "Delete All Data",
                        subtitle = "Permanently delete all habits and streaks",
                        icon = Icons.Default.DeleteForever,
                        titleColor = AppColors.AccentRed,
                        onClick = { viewModel.toggleDeleteConfirmation(true) }
                    )
                }
            }

            // --- ABOUT ---
            SettingsSectionHeader(title = "About", icon = Icons.Default.Info)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Streak Habit Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Version ${uiState.appVersion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))

                    HorizontalDivider(color = AppColors.BorderGlass)
                    Spacer(Modifier.height(12.dp))

                    // Privacy Policy
                    Text(
                        "Privacy Policy",
                        style = MaterialTheme.typography.titleSmall,
                        color = AppColors.AccentPurple
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "All data is stored locally on your device.\n" +
                        "No data is sent to any server.\n" +
                        "Zero analytics, zero Firebase, zero ads.\n" +
                        "Your privacy is fully respected.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))

                    HorizontalDivider(color = AppColors.BorderGlass)
                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Made with 🔥 by Shahadat Hasan",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleDeleteConfirmation(false) },
            title = { Text("Delete All Data?", color = AppColors.TextPrimary) },
            text = {
                Text(
                    "This will permanently delete ALL habits, groups, and streak data. This action cannot be undone.",
                    color = AppColors.TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteAllData() }) {
                    Text("Delete Everything", color = AppColors.AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleDeleteConfirmation(false) }) {
                    Text("Cancel", color = AppColors.TextSecondary)
                }
            },
            containerColor = AppColors.SurfaceElevated
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = AppColors.AccentPurple,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = AppColors.AccentPurple
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    titleColor: androidx.compose.ui.graphics.Color = AppColors.TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AppColors.TextTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}
