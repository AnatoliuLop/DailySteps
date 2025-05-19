package com.example.dailysteps.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.R
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.domain.usecase.tasks.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PlanViewModel(
    prefs: PreferencesManager,
    private val context: Context,               // ← добавили
    private val getTasks: GetTasksUseCase,
    private val addTask: AddTaskUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val deleteTask: DeleteTaskUseCase
) : ViewModel() {

    private val fmt = DateTimeFormatter.ISO_DATE
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error



    // Сначала читаем строку из prefs (или today)
    private val dateIsoFlow: Flow<String> = prefs.lastDate
        .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(fmt) }

    // Для получения тасков всё ещё конвертируем в LocalDate
    val tasks: StateFlow<List<DailyTask>> = dateIsoFlow
        .map { LocalDate.parse(it, fmt) }
        .flatMapLatest { date -> getTasks(date) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(description: String, category: String = "general") {
        viewModelScope.launch {
            try {
                val dateIso = dateIsoFlow.first()
                addTask(description, category, dateIso, defaultTaskId = 0)
            } catch (e: DuplicateTaskException) {
                val msg = context.getString(R.string.error_task_exists)
                _error.emit(msg)
            }
        }
    }

    fun changeNote(task: DailyTask, note: String) {
        viewModelScope.launch { updateTask(task.copy(note = note)) }
    }

    fun editDescription(task: DailyTask, newDesc: String) {
        viewModelScope.launch { updateTask(task.copy(description = newDesc)) }
    }

    fun remove(task: DailyTask) {
        viewModelScope.launch { deleteTask(task) }
    }
}
