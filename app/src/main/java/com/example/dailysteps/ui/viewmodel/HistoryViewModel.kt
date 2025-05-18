package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.data.model.DateRateEntity
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.domain.usecase.tasks.GetCompletionRatesUseCase
import com.example.dailysteps.domain.usecase.tasks.GetHistoryDatesUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryViewModel(
    prefs: PreferencesManager,
    private val getDatesUseCase: GetHistoryDatesUseCase,
    private val getRatesUseCase: GetCompletionRatesUseCase
) : ViewModel() {
    private val fmt = DateTimeFormatter.ISO_DATE

    // 1) поток даты ISO
    private val toIsoFlow: Flow<String> = prefs.lastDate
        .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(fmt) }

    // 2) поток from = то же минус 29 дней
    private val fromIsoFlow: Flow<String> = toIsoFlow
        .map { LocalDate.parse(it, fmt).minusDays(29).format(fmt) }

    // 3) список всех дат без изменений
    val dates: StateFlow<List<String>> = getDatesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 4) история за диапазон [from..to]
    val historyRates: StateFlow<List<DateRateEntity>> =
        combine(fromIsoFlow, toIsoFlow) { fromIso, toIso ->
            getRatesUseCase(fromIso, toIso)
        }
            .flatMapLatest { it }  // unwrap Flow<List<…>>
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}