package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.domain.usecase.GetStreakUseCase
import com.example.dailysteps.domain.usecase.GetStepEntryUseCase
import com.example.dailysteps.domain.usecase.UpdateStepEntryUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StepUiState(
    val goal: Int = 0,
    val actual: Int = 0
)

class MainMenuViewModel(
    getStreak: GetStreakUseCase,
    getStepEntry: GetStepEntryUseCase,
    private val updateStepEntry: UpdateStepEntryUseCase
) : ViewModel() {

    // Стрик дней
    val streak: StateFlow<Int> = getStreak()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Преобразуем Flow<StepEntry?> в Flow<StepUiState>
    val stepState: StateFlow<StepUiState> = getStepEntry()
        .map { entry ->
            // Если записи нет — goal = 0, actual = 0
            StepUiState(
                goal = entry?.goal ?: 0,
                actual = entry?.actual ?: 0
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = StepUiState()
        )

    /** Вызывается при обновлении показаний шагомера */
    fun onStepCountUpdated(count: Int) {
        viewModelScope.launch {
            updateStepEntry(count)
        }
    }
}
