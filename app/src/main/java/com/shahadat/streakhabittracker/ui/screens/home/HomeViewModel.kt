package com.shahadat.streakhabittracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahadat.streakhabittracker.data.db.entities.Habit
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import com.shahadat.streakhabittracker.data.db.entities.HabitLog
import com.shahadat.streakhabittracker.data.repository.HabitRepository
import com.shahadat.streakhabittracker.ui.components.HabitCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val habits: List<HabitCardState> = emptyList(),
    val groups: List<HabitGroup> = emptyList(),
    val selectedGroupId: Long? = null,
    val isLoading: Boolean = true,
    val lastCompletedHabitId: Long? = null, // for undo snackbar
    val hasLockedGroups: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    private val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Combine groups and habits reactively
            combine(
                repository.getUnlockedGroups(),
                repository.getUnlockedHabits(),
                repository.getLogsForDate(todayDate),
                _selectedGroupId
            ) { groups, habits, todayLogs, selectedGroupId ->
                val filteredHabits = if (selectedGroupId != null) {
                    habits.filter { it.groupId == selectedGroupId }
                } else {
                    habits
                }

                val todayLogSet = todayLogs.map { it.habitId }.toSet()

                val habitCardStates = filteredHabits.map { habit ->
                    val group = groups.find { it.id == habit.groupId }
                    val streak = repository.calculateStreak(habit.id)

                    HabitCardState(
                        id = habit.id,
                        name = habit.name,
                        groupName = group?.name ?: "",
                        groupColorHex = group?.colorHex ?: "#8B7CF6",
                        streak = streak,
                        isCompletedToday = todayLogSet.contains(habit.id),
                        reminderTime = habit.reminderTime
                    )
                }

                HomeUiState(
                    habits = habitCardStates,
                    groups = groups,
                    selectedGroupId = selectedGroupId,
                    isLoading = false,
                    hasLockedGroups = true // Will be checked separately
                )
            }.collect { state ->
                _uiState.value = state
            }
        }

        // Check for locked groups
        viewModelScope.launch {
            repository.getLockedGroups().collect { lockedGroups ->
                _uiState.update { it.copy(hasLockedGroups = lockedGroups.isNotEmpty()) }
            }
        }
    }

    fun selectGroup(groupId: Long?) {
        _selectedGroupId.value = groupId
    }

    fun completeHabit(habitId: Long) {
        viewModelScope.launch {
            val log = HabitLog(
                habitId = habitId,
                completedDate = todayDate
            )
            repository.insertLog(log)
            _uiState.update { it.copy(lastCompletedHabitId = habitId) }
        }
    }

    fun uncompleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteLogByHabitAndDate(habitId, todayDate)
        }
    }

    fun undoLastCompletion() {
        viewModelScope.launch {
            val habitId = _uiState.value.lastCompletedHabitId ?: return@launch
            repository.deleteLogByHabitAndDate(habitId, todayDate)
            _uiState.update { it.copy(lastCompletedHabitId = null) }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabitById(habitId)
        }
    }

    fun clearLastCompleted() {
        _uiState.update { it.copy(lastCompletedHabitId = null) }
    }
}
