package com.example.dailysteps.domain.usecase

import com.example.dailysteps.data.model.DailyDayNote
import com.example.dailysteps.data.repository.DailyDayNoteRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SaveDayNoteUseCase(
    private val repo: DailyDayNoteRepository
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    suspend operator fun invoke(noteText: String) {
        val date = LocalDate.now().format(fmt)
        repo.insert(DailyDayNote(date = date, note = noteText))
    }
}
