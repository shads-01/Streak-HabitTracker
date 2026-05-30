package com.shahadat.streakhabittracker.ui.screens.locked

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahadat.streakhabittracker.data.db.entities.HabitLog
import com.shahadat.streakhabittracker.data.repository.HabitRepository
import com.shahadat.streakhabittracker.ui.components.HabitCardState
import com.shahadat.streakhabittracker.util.SecurityUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class LockUiState(
    val isVaultSetup: Boolean = false,
    val lockType: String = "pin",  // "pin" or "pattern"
    val enteredPin: String = "",
    val isSettingUp: Boolean = false,
    val setupPin: String = "",
    val confirmPin: String = "",
    val setupStep: SetupStep = SetupStep.ENTER,
    val errorMessage: String? = null,
    val wrongAttempts: Int = 0,
    val isUnlocked: Boolean = false,
    val shakeAnimation: Boolean = false
)

enum class SetupStep {
    ENTER,      // Enter new PIN/pattern
    CONFIRM     // Confirm new PIN/pattern
}

data class LockedVaultUiState(
    val habits: List<HabitCardState> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class LockViewModel @Inject constructor(
    private val application: Application,
    private val repository: HabitRepository
) : ViewModel() {

    private val _lockState = MutableStateFlow(LockUiState())
    val lockState: StateFlow<LockUiState> = _lockState.asStateFlow()

    private val _vaultState = MutableStateFlow(LockedVaultUiState())
    val vaultState: StateFlow<LockedVaultUiState> = _vaultState.asStateFlow()

    private val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    init {
        checkVaultSetup()
    }

    private fun checkVaultSetup() {
        val isSetup = SecurityUtils.isVaultSetup(application)
        val lockType = SecurityUtils.getLockType(application)
        _lockState.update {
            it.copy(
                isVaultSetup = isSetup,
                lockType = lockType,
                isSettingUp = !isSetup
            )
        }
    }

    fun onPinDigitEntered(digit: String) {
        val state = _lockState.value

        if (state.isSettingUp) {
            when (state.setupStep) {
                SetupStep.ENTER -> {
                    val newPin = state.setupPin + digit
                    _lockState.update { it.copy(setupPin = newPin, errorMessage = null) }
                    if (newPin.length >= 4) {
                        _lockState.update { it.copy(setupStep = SetupStep.CONFIRM, errorMessage = null) }
                    }
                }
                SetupStep.CONFIRM -> {
                    val confirmPin = state.confirmPin + digit
                    _lockState.update { it.copy(confirmPin = confirmPin, errorMessage = null) }
                    if (confirmPin.length >= state.setupPin.length) {
                        if (confirmPin == state.setupPin) {
                            // PINs match — save
                            SecurityUtils.setVaultCredential(application, confirmPin, "pin")
                            _lockState.update {
                                it.copy(
                                    isVaultSetup = true,
                                    isSettingUp = false,
                                    isUnlocked = true,
                                    errorMessage = null
                                )
                            }
                        } else {
                            _lockState.update {
                                it.copy(
                                    confirmPin = "",
                                    setupStep = SetupStep.ENTER,
                                    setupPin = "",
                                    errorMessage = "PINs don't match. Try again.",
                                    shakeAnimation = true
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Verification mode
            val newPin = state.enteredPin + digit
            _lockState.update { it.copy(enteredPin = newPin, errorMessage = null) }

            if (newPin.length >= 4) {
                if (SecurityUtils.verifyCredential(application, newPin)) {
                    _lockState.update { it.copy(isUnlocked = true) }
                } else {
                    val newAttempts = state.wrongAttempts + 1
                    _lockState.update {
                        it.copy(
                            enteredPin = "",
                            wrongAttempts = newAttempts,
                            errorMessage = if (newAttempts >= 5) "Too many attempts" else "Wrong PIN",
                            shakeAnimation = true
                        )
                    }
                }
            }
        }
    }

    fun onPinBackspace() {
        val state = _lockState.value
        if (state.isSettingUp) {
            when (state.setupStep) {
                SetupStep.ENTER -> {
                    if (state.setupPin.isNotEmpty()) {
                        _lockState.update { it.copy(setupPin = state.setupPin.dropLast(1)) }
                    }
                }
                SetupStep.CONFIRM -> {
                    if (state.confirmPin.isNotEmpty()) {
                        _lockState.update { it.copy(confirmPin = state.confirmPin.dropLast(1)) }
                    }
                }
            }
        } else {
            if (state.enteredPin.isNotEmpty()) {
                _lockState.update { it.copy(enteredPin = state.enteredPin.dropLast(1)) }
            }
        }
    }

    fun clearShakeAnimation() {
        _lockState.update { it.copy(shakeAnimation = false) }
    }

    fun loadLockedHabits() {
        viewModelScope.launch {
            combine(
                repository.getLockedGroups(),
                repository.getLockedHabits(),
                repository.getLogsForDate(todayDate)
            ) { groups, habits, todayLogs ->
                val todayLogSet = todayLogs.map { it.habitId }.toSet()

                val habitCards = habits.map { habit ->
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

                LockedVaultUiState(habits = habitCards, isLoading = false)
            }.collect { state ->
                _vaultState.value = state
            }
        }
    }

    fun completeLockedHabit(habitId: Long) {
        viewModelScope.launch {
            repository.insertLog(HabitLog(habitId = habitId, completedDate = todayDate))
        }
    }

    fun uncompleteLockedHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteLogByHabitAndDate(habitId, todayDate)
        }
    }
}
