package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.DateRateEntity
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.domain.usecase.daynote.GetDayNoteUseCase
import com.example.dailysteps.domain.usecase.tasks.GetCompletionRatesUseCase
import com.example.dailysteps.domain.usecase.tasks.GetHistoryDatesUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class HistoryViewModel(
    prefs: PreferencesManager,
    private val getDates:       GetHistoryDatesUseCase,
    private val getRates:       GetCompletionRatesUseCase,
    private val getTasks:       GetTasksUseCase,
    private val getDayNote:     GetDayNoteUseCase
) : ViewModel() {
    private val fmt = DateTimeFormatter.ISO_DATE


    private val toIsoFlow = prefs.lastDate
        .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(fmt) }
    private val fromIsoFlow = toIsoFlow
        .map { LocalDate.parse(it, fmt).minusDays(29).format(fmt) }


    val calendarDates: StateFlow<List<String>> =
        getDates()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val historyRates: StateFlow<List<DateRateEntity>> =
        fromIsoFlow
            .combine(toIsoFlow) { fromIso, toIso -> fromIso to toIso }
            .flatMapLatest { (fromIso, toIso) ->
                getRates(fromIso, toIso)      // Flow<List<DateRateEntity>>
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate


    val tasksForDate: StateFlow<List<DailyTask>> = _selectedDate
        .filterNotNull()
        .map { LocalDate.parse(it, fmt) }
        .flatMapLatest { date -> getTasks(date) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val dayNoteForDate: StateFlow<String> = _selectedDate
        .filterNotNull()
        .flatMapLatest { iso ->
            getDayNote(iso)
                .map { it?.note.orEmpty() }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")


    fun selectDate(iso: String) {
        viewModelScope.launch { _selectedDate.value = iso }
    }


    fun clearSelection() {
        viewModelScope.launch { _selectedDate.value = null }
    }
}