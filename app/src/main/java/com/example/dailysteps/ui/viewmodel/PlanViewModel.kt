package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.domain.usecase.AddTaskUseCase
import com.example.dailysteps.domain.usecase.GetTasksUseCase
import com.example.dailysteps.domain.usecase.ToggleDoneUseCase
import com.example.dailysteps.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class PlanViewModel(
    private val getTasks: GetTasksUseCase,
    private val addTask: AddTaskUseCase,
    private val toggleDone: ToggleDoneUseCase,
    private val updateTask: UpdateTaskUseCase     // новый use-case
) : ViewModel() {

    val tasks: StateFlow<List<DailyTask>> =
        getTasks(LocalDate.now())
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(description: String, category: String = "general") {
        viewModelScope.launch {
            addTask(description, category)
        }
    }

    fun toggle(task: DailyTask) {
        viewModelScope.launch {
            toggleDone(task)
        }
    }
    fun changeNote(task: DailyTask, note: String) {
        viewModelScope.launch {
            toggleDone(task) // нет, здесь иное
            updateTask(task.copy(note = note))
        }
    }
}
