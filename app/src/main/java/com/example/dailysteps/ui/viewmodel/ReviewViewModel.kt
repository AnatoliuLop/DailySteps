// ReviewViewModel.kt
package com.example.dailysteps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.domain.usecase.daynote.GetDayNoteUseCase
import com.example.dailysteps.domain.usecase.daynote.SaveDayNoteUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import com.example.dailysteps.domain.usecase.tasks.ToggleDoneUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReviewViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val toggleDoneUseCase: ToggleDoneUseCase,
    private val getDayNoteUseCase: GetDayNoteUseCase,
    private val saveDayNoteUseCase: SaveDayNoteUseCase
) : ViewModel() {

    val tasks: StateFlow<List<DailyTask>> =
        getTasksUseCase(LocalDate.now())
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val dayNote: StateFlow<String> =
        getDayNoteUseCase()
            .map { it?.note ?: "" }
            .stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun toggle(task: DailyTask) = viewModelScope.launch {
        toggleDoneUseCase(task)
    }

    fun saveNote(text: String) = viewModelScope.launch {
        saveDayNoteUseCase(text)
    }

    // 1) Новый flow для сообщения
    private val _completionMessage = MutableStateFlow<String?>(null)
    val completionMessage: StateFlow<String?> = _completionMessage.asStateFlow()

    // 2) completeDay считает прогресс и выставляет сообщение
    fun completeDay() = viewModelScope.launch {
        val todayTasks = getTasksUseCase(LocalDate.now()).first()
        val total = todayTasks.size
        val doneCount = todayTasks.count { it.done }

        val msg = when {
            doneCount == 1 -> "Хорошая работа, начало положено."
            total > 0 && doneCount * 2 == total -> "Отлично, половина запланированных заданий уже позади."
            total > 1 && doneCount == total - 1 ->
                "Осталось только 1 задание на сегодня, нужно успеть доделать и наслаждаться отдыхом."
            doneCount == total -> "Супер! Этот день был продуктивен, ты сделал всё, что запланировал. Запишем это в историю."
            doneCount in 2 until total -> "Супер, мы всё ближе к цели!"
            else -> "Постарайся выполнить хотя бы одно задание."
        }

        _completionMessage.value = msg
        // Здесь же можно засейвить flag в preferences или историю
    }

    // 3) сброс сообщения (при закрытии диалога)
    fun clearCompletionMessage() = viewModelScope.launch {
        _completionMessage.value = null
    }
}
