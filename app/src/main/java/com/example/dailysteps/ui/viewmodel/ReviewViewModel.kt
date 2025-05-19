// ReviewViewModel.kt
package com.example.dailysteps.ui.viewmodel

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysteps.R
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.domain.usecase.daynote.*
import com.example.dailysteps.domain.usecase.tasks.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReviewViewModel(
    private val context: Context,
    prefs: PreferencesManager,
    private val getTasksUseCase: GetTasksUseCase,
    private val toggleDoneUseCase: ToggleDoneUseCase,
    private val getDayNoteUseCase: GetDayNoteUseCase,
    private val saveDayNoteUseCase: SaveDayNoteUseCase
) : ViewModel() {

    private val iso = DateTimeFormatter.ISO_DATE

    private val dateFlow: Flow<LocalDate> = prefs.lastDate
        .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(iso) }
        .map { LocalDate.parse(it, iso) }

    val tasks: StateFlow<List<DailyTask>> = dateFlow
        .flatMapLatest { date -> getTasksUseCase(date) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val dayNote: StateFlow<String> = getDayNoteUseCase()
        .map { it?.note.orEmpty() }
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
        val date      = dateFlow.first()
        val todayTasks = getTasksUseCase(date).first()
        val total     = todayTasks.size
        val doneCount = todayTasks.count { it.done }

        // выбираем ID строки
        val msgRes = when {
            doneCount == 1 ->
                R.string.review_msg_first_step
            total > 0 && doneCount * 2 == total ->
                R.string.review_msg_halfway
            total > 1 && doneCount == total - 1 ->
                R.string.review_msg_one_left
            doneCount == total ->
                R.string.review_msg_all_done
            doneCount in 2 until total ->
                R.string.review_msg_closer
            else ->
                R.string.review_msg_try_one
        }

        // грузим сам текст из ресурсов
        _completionMessage.value = context.getString(msgRes)
    }
    // 3) сброс сообщения (при закрытии диалога)
    fun clearCompletionMessage() = viewModelScope.launch {
        _completionMessage.value = null
    }
}
