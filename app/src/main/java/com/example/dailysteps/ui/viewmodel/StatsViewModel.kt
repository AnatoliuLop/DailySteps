package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.domain.usecase.stats.GetStreakUseCase
import com.example.dailysteps.domain.usecase.stats.GetTaskStreaksUseCase
import com.example.dailysteps.domain.usecase.tasks.GetCompletionRatesUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(
    prefs: PreferencesManager,
    private val getRates: GetCompletionRatesUseCase,
    private val getStreak: GetStreakUseCase,
    private val getTaskStreaks: GetTaskStreaksUseCase
) : ViewModel() {
    private val fmt = DateTimeFormatter.ISO_DATE

    // 1) поток даты ISO
    private val dateIsoFlow: Flow<String> = prefs.lastDate
        .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(fmt) }

    // 2) процент за «рабочий» день
    val percentDone: StateFlow<Float> = dateIsoFlow
        .flatMapLatest { iso ->
            getRates(iso, iso)
        }
        .map { list -> list.firstOrNull()?.pct?.toFloat() ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    // 3) стрик: если хотите считать стрик _до_ того же дня,
    //    переделайте GetStreakUseCase, чтобы он принимал датуIso
    //    или здесь просто дергайте dateIsoFlow и внутри GetStreakUseCase
    val streak: StateFlow<Int> = getStreak()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // 4) taskStreaks без изменений
    val taskStreaks: StateFlow<List<TaskStreak>> = getTaskStreaks()
        .map { domainList -> domainList.map { TaskStreak(it.name, it.days) } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}