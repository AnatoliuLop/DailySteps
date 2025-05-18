// app/src/main/java/com/example/dailysteps/domain/usecase/daynote/SaveDayNoteUseCase.kt
package com.example.dailysteps.domain.usecase.daynote

import com.example.dailysteps.data.model.DailyDayNote
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.data.repository.DailyDayNoteRepository
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter

class SaveDayNoteUseCase(
    private val repo: DailyDayNoteRepository,
    private val prefs: PreferencesManager
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    /** Записывает заметку для той же prefs.lastDate */
    suspend operator fun invoke(noteText: String) {
        val iso = prefs.lastDate.first().takeIf(String::isNotBlank)
            ?: java.time.LocalDate.now().format(fmt)
        repo.insert(DailyDayNote(date = iso, note = noteText))
    }
}
