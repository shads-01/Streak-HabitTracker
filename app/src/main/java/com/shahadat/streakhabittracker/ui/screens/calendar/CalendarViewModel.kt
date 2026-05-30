package com.shahadat.streakhabittracker.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahadat.streakhabittracker.data.db.entities.Habit
import com.shahadat.streakhabittracker.data.db.entities.HabitGroup
import com.shahadat.streakhabittracker.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HabitWithStreak(
    val habit: Habit,
    val group: HabitGroup?,
    val streak: Int,
    val totalCompletions: Int,
    val completedDates: Set<String>
)

data class CalendarUiState(
    val habitsByGroup: Map<HabitGroup, List<HabitWithStreak>> = emptyMap(),
    val isLoading: Boolean = true
)

data class HabitCalendarUiState(
    val habit: Habit? = null,
    val group: HabitGroup? = null,
    val streak: Int = 0,
    val totalCompletions: Int = 0,
    val currentMonth: YearMonth = YearMonth.now(),
    val completedDatesInMonth: Set<LocalDate> = emptySet(),
    val allCompletedDates: Set<LocalDate> = emptySet(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getUnlockedGroups(),
                repository.getUnlockedHabits()
            ) { groups, habits ->
                val habitsByGroup = mutableMapOf<HabitGroup, List<HabitWithStreak>>()

                for (group in groups) {
                    val groupHabits = habits.filter { it.groupId == group.id }
                    val habitsWithStreaks = groupHabits.map { habit ->
                        val streak = repository.calculateStreak(habit.id)
                        val totalCompletions = repository.getTotalCompletionsForHabit(habit.id)
                        val completedDates = repository.getCompletedDatesForHabit(habit.id).toSet()

                        HabitWithStreak(
                            habit = habit,
                            group = group,
                            streak = streak,
                            totalCompletions = totalCompletions,
                            completedDates = completedDates
                        )
                    }
                    if (habitsWithStreaks.isNotEmpty()) {
                        habitsByGroup[group] = habitsWithStreaks
                    }
                }

                CalendarUiState(
                    habitsByGroup = habitsByGroup,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

@HiltViewModel
class HabitCalendarViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitCalendarUiState())
    val uiState: StateFlow<HabitCalendarUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun loadHabit(habitId: Long) {
        viewModelScope.launch {
            val habit = repository.getHabitById(habitId) ?: return@launch
            val group = repository.getGroupById(habit.groupId)
            val streak = repository.calculateStreak(habitId)
            val totalCompletions = repository.getTotalCompletionsForHabit(habitId)
            val allDates = repository.getCompletedDatesForHabit(habitId)
                .map { LocalDate.parse(it, dateFormatter) }
                .toSet()

            val currentMonth = YearMonth.now()
            val monthDates = allDates.filter {
                YearMonth.from(it) == currentMonth
            }.toSet()

            _uiState.value = HabitCalendarUiState(
                habit = habit,
                group = group,
                streak = streak,
                totalCompletions = totalCompletions,
                currentMonth = currentMonth,
                completedDatesInMonth = monthDates,
                allCompletedDates = allDates,
                isLoading = false
            )
        }
    }

    fun changeMonth(yearMonth: YearMonth) {
        val allDates = _uiState.value.allCompletedDates
        val monthDates = allDates.filter {
            YearMonth.from(it) == yearMonth
        }.toSet()

        _uiState.update {
            it.copy(
                currentMonth = yearMonth,
                completedDatesInMonth = monthDates
            )
        }
    }
}
