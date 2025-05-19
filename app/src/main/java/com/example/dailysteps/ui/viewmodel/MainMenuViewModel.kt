package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.domain.usecase.stats.GetStreakUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StepUiState(
    val goal: Int = 0,
    val actual: Int = 0
)

class MainMenuViewModel(
    getStreak: GetStreakUseCase
) : ViewModel() {

    // Стрик дней
    val streak: StateFlow<Int> = getStreak()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

}
