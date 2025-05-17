package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.domain.usecase.stats.GetStreakUseCase
import com.example.dailysteps.domain.usecase.stats.GetTaskStreaksUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(
    getTasks: GetTasksUseCase,
    getStreak: GetStreakUseCase,
    private val getTaskStreaksUseCase: GetTaskStreaksUseCase
) : ViewModel() {
    private val fmt = DateTimeFormatter.ISO_DATE

    // процент выполнения сегодня
    val percentDone: StateFlow<Float> = getTasks(LocalDate.now())
        .map { list ->
            if (list.isEmpty()) 0f
            else list.count { it.done }.toFloat() / list.size
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    // общий стрик дней, когда все задачи выполнялись
    val streak: StateFlow<Int> = getStreak()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // стрики по каждой задаче: конвертируем DomainTaskStreak ➡ TaskStreak (UI-модель)
    val taskStreaks: StateFlow<List<TaskStreak>> =
        getTaskStreaksUseCase()                                      // Flow<List<DomainTaskStreak>>
            .map { domainList ->
                domainList.map { domain ->
                    TaskStreak(domain.name, domain.days)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
