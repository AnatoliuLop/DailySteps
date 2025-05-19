package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.domain.usecase.stats.GetStreakUseCase
import com.example.dailysteps.domain.usecase.stats.GetTaskStreaksUseCase
import com.example.dailysteps.domain.usecase.stats.GetWeeklyCompletionUseCase
import com.example.dailysteps.domain.usecase.stats.TaskWeeklyCount
import com.example.dailysteps.domain.usecase.tasks.GetCompletionRatesUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(
    prefs: PreferencesManager,
    private val getRates: GetCompletionRatesUseCase,
    private val getStreak: GetStreakUseCase,
    private val getTaskStreaks: GetTaskStreaksUseCase,
    private val getWeekly: GetWeeklyCompletionUseCase
) : ViewModel() {
    private val fmt = DateTimeFormatter.ISO_DATE


    private val dateIsoFlow: Flow<String> = prefs.lastDate
        .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(fmt) }


    val percentDone: StateFlow<Float> = dateIsoFlow
        .flatMapLatest { iso ->
            getRates(iso, iso)
        }
        .map { list -> list.firstOrNull()?.pct?.toFloat() ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)


    val streak: StateFlow<Int> = getStreak()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)


    val taskStreaks: StateFlow<List<TaskStreak>> = getTaskStreaks()
        .map { domainList -> domainList.map { TaskStreak(it.name, it.days) } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val weeklyStats: StateFlow<List<TaskWeeklyCount>> = dateIsoFlow
        .flatMapLatest { iso ->
            val fromIso = LocalDate.parse(iso, fmt)
                .minusWeeks(1)
                .format(fmt)
            getWeekly(fromIso, iso)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}
