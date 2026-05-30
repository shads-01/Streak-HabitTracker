package com.shahadat.streakhabittracker.ui.screens.addhabit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahadat.streakhabittracker.data.db.entities.Habit
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import com.shahadat.streakhabittracker.data.repository.HabitRepository
import com.shahadat.streakhabittracker.ui.theme.AppColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddHabitUiState(
    val habitName: String = "",
    val selectedGroupId: Long? = null,
    val reminderTime: String? = null,
    val reminderEnabled: Boolean = false,
    val groups: List<HabitGroup> = emptyList(),
    val isEditing: Boolean = false,
    val showGroupDialog: Boolean = false,
    val newGroupName: String = "",
    val newGroupColorIndex: Int = 0,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val isLockedGroup: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    private var editingHabitId: Long? = null

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            repository.getAllGroups().collect { groups ->
                _uiState.update { it.copy(groups = groups) }
            }
        }
    }

    fun loadHabitForEditing(habitId: Long) {
        viewModelScope.launch {
            val habit = repository.getHabitById(habitId) ?: return@launch
            editingHabitId = habitId
            _uiState.update {
                it.copy(
                    habitName = habit.name,
                    selectedGroupId = habit.groupId,
                    reminderTime = habit.reminderTime,
                    reminderEnabled = habit.reminderEnabled,
                    isEditing = true
                )
            }
        }
    }

    fun updateHabitName(name: String) {
        _uiState.update { it.copy(habitName = name, errorMessage = null) }
    }

    fun selectGroup(groupId: Long) {
        _uiState.update { it.copy(selectedGroupId = groupId, errorMessage = null) }
    }

    fun updateReminderTime(time: String?) {
        _uiState.update { it.copy(reminderTime = time) }
    }

    fun toggleReminder(enabled: Boolean) {
        _uiState.update { it.copy(reminderEnabled = enabled) }
    }

    fun toggleGroupDialog(show: Boolean) {
        _uiState.update {
            it.copy(
                showGroupDialog = show,
                newGroupName = "",
                newGroupColorIndex = 0,
                isLockedGroup = false
            )
        }
    }

    fun updateNewGroupName(name: String) {
        _uiState.update { it.copy(newGroupName = name) }
    }

    fun updateNewGroupColorIndex(index: Int) {
        _uiState.update { it.copy(newGroupColorIndex = index) }
    }

    fun toggleLockedGroup(locked: Boolean) {
        _uiState.update { it.copy(isLockedGroup = locked) }
    }

    fun createGroup() {
        val state = _uiState.value
        if (state.newGroupName.isBlank()) return

        viewModelScope.launch {
            val colorHex = AppColors.GroupColors.getOrElse(state.newGroupColorIndex) {
                AppColors.GroupColors.first()
            }.let { color ->
                String.format("#%06X", 0xFFFFFF and color.hashCode())
            }

            val groupId = repository.insertGroup(
                HabitGroup(
                    name = state.newGroupName.trim(),
                    colorHex = colorHex,
                    isLocked = state.isLockedGroup
                )
            )

            _uiState.update {
                it.copy(
                    selectedGroupId = groupId,
                    showGroupDialog = false,
                    newGroupName = "",
                    newGroupColorIndex = 0,
                    isLockedGroup = false
                )
            }
        }
    }

    fun saveHabit() {
        val state = _uiState.value

        if (state.habitName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Habit name cannot be empty") }
            return
        }

        if (state.selectedGroupId == null) {
            _uiState.update { it.copy(errorMessage = "Please select a group") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                if (state.isEditing && editingHabitId != null) {
                    val existingHabit = repository.getHabitById(editingHabitId!!)
                    if (existingHabit != null) {
                        repository.updateHabit(
                            existingHabit.copy(
                                name = state.habitName.trim(),
                                groupId = state.selectedGroupId,
                                reminderTime = if (state.reminderEnabled) state.reminderTime else null,
                                reminderEnabled = state.reminderEnabled
                            )
                        )
                    }
                } else {
                    repository.insertHabit(
                        Habit(
                            name = state.habitName.trim(),
                            groupId = state.selectedGroupId,
                            reminderTime = if (state.reminderEnabled) state.reminderTime else null,
                            reminderEnabled = state.reminderEnabled
                        )
                    )
                }

                _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = "Failed to save: ${e.message}")
                }
            }
        }
    }
}
