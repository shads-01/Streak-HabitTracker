package com.shahadat.streakhabittracker.ui.screens.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahadat.streakhabittracker.data.db.entities.Habit
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import com.shahadat.streakhabittracker.data.db.entities.HabitLog
import com.shahadat.streakhabittracker.data.repository.HabitRepository
import com.shahadat.streakhabittracker.util.SecurityUtils
import com.shahadat.streakhabittracker.util.WallpaperManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class SettingsUiState(
    val hasWallpaper: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val isVaultSetup: Boolean = false,
    val autoLockDuration: String = "immediate",
    val appVersion: String = "1.0.0",
    val showDeleteConfirmation: Boolean = false,
    val showExportSuccess: Boolean = false,
    val showImportSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Export/import data model.
 */
data class AppData(
    val groups: List<HabitGroup>,
    val habits: List<Habit>,
    val logs: List<HabitLog>
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application,
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val gson = Gson()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            WallpaperManager.hasWallpaper(application).collect { hasWallpaper ->
                _uiState.update { it.copy(hasWallpaper = hasWallpaper) }
            }
        }

        viewModelScope.launch {
            WallpaperManager.getNotificationsEnabled(application).collect { enabled ->
                _uiState.update { it.copy(notificationsEnabled = enabled) }
            }
        }

        _uiState.update {
            it.copy(
                isVaultSetup = SecurityUtils.isVaultSetup(application),
                autoLockDuration = SecurityUtils.getAutoLockDuration(application)
            )
        }
    }

    fun setWallpaper(uri: Uri) {
        viewModelScope.launch {
            WallpaperManager.setWallpaper(application, uri)
        }
    }

    fun removeWallpaper() {
        viewModelScope.launch {
            WallpaperManager.removeWallpaper(application)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            WallpaperManager.setNotificationsEnabled(application, enabled)
        }
    }

    fun setAutoLockDuration(duration: String) {
        SecurityUtils.setAutoLockDuration(application, duration)
        _uiState.update { it.copy(autoLockDuration = duration) }
    }

    fun resetVault() {
        SecurityUtils.clearVault(application)
        _uiState.update { it.copy(isVaultSetup = false) }
    }

    fun toggleDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirmation = show) }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            try {
                // Delete all groups (cascade deletes habits and logs)
                val groups = repository.getAllGroupsSync()
                groups.forEach { repository.deleteGroup(it) }
                _uiState.update { it.copy(showDeleteConfirmation = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete data: ${e.message}") }
            }
        }
    }

    fun exportData(): String? {
        var json: String? = null
        viewModelScope.launch {
            try {
                val groups = repository.getAllGroupsSync()
                val habits = repository.getAllHabitsSync()
                val logs = repository.getAllLogs()

                val appData = AppData(groups, habits, logs)
                json = gson.toJson(appData)

                // Save to Downloads directory
                val exportFile = File(
                    application.getExternalFilesDir(null),
                    "streak_backup_${System.currentTimeMillis()}.json"
                )
                exportFile.writeText(json!!)

                _uiState.update { it.copy(showExportSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Export failed: ${e.message}") }
            }
        }
        return json
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = application.contentResolver.openInputStream(uri)
                val json = inputStream?.bufferedReader()?.readText() ?: return@launch
                inputStream.close()

                val type = object : TypeToken<AppData>() {}.type
                val appData: AppData = gson.fromJson(json, type)

                // Import groups first, then habits, then logs
                appData.groups.forEach { group ->
                    repository.insertGroup(group.copy(id = 0))
                }
                appData.habits.forEach { habit ->
                    repository.insertHabit(habit.copy(id = 0))
                }
                appData.logs.forEach { log ->
                    repository.insertLog(log.copy(id = 0))
                }

                _uiState.update { it.copy(showImportSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Import failed: ${e.message}") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                showExportSuccess = false,
                showImportSuccess = false,
                errorMessage = null
            )
        }
    }
}
