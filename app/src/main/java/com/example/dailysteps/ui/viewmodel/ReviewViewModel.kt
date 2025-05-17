package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.DailyDayNote
import com.example.dailysteps.domain.usecase.GetDayNoteUseCase
import com.example.dailysteps.domain.usecase.GetTasksUseCase
import com.example.dailysteps.domain.usecase.SaveDayNoteUseCase
import com.example.dailysteps.domain.usecase.ToggleDoneUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DayNoteUiState(val note: String = "")

class ReviewViewModel(
    getTasks: GetTasksUseCase,
    private val toggleDone: ToggleDoneUseCase,
    getNote: GetDayNoteUseCase,
    private val saveNote: SaveDayNoteUseCase
) : ViewModel() {

    val tasks: StateFlow<List<DailyTask>> =
        getTasks(LocalDate.now())
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val dayNote: StateFlow<DailyDayNote?> =
        getNote()
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun toggle(task: DailyTask) {
        viewModelScope.launch {
            toggleDone(task)
        }
    }

    fun saveNote(text: String) {
        viewModelScope.launch {
            saveNote(text)
        }
    }
}
