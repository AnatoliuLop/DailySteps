package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.domain.usecase.tasks.AddTaskUseCase
import com.example.dailysteps.domain.usecase.tasks.DeleteTaskUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import com.example.dailysteps.domain.usecase.tasks.ToggleDoneUseCase
import com.example.dailysteps.domain.usecase.tasks.UpdateTaskUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class PlanViewModel(
    private val getTasks: GetTasksUseCase,
    private val addTask: AddTaskUseCase,

    private val updateTask: UpdateTaskUseCase,
    private val deleteTask: DeleteTaskUseCase
) : ViewModel() {

    val tasks: StateFlow<List<DailyTask>> =
        getTasks(LocalDate.now())
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(description: String, category: String = "general") {
        viewModelScope.launch { addTask(description, category) }
    }

    fun changeNote(task: DailyTask, note: String) {
        viewModelScope.launch {
            updateTask(task.copy(note = note))
        }
    }

    fun editDescription(task: DailyTask, newDesc: String) = viewModelScope.launch {
        updateTask(task.copy(description = newDesc))
    }

    fun remove(task: DailyTask) = viewModelScope.launch {
        deleteTask(task)
    }
}

