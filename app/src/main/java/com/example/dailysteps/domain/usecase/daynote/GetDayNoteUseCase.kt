package com.example.dailysteps.domain.usecase.daynote

import com.example.dailysteps.data.model.DailyDayNote
import com.example.dailysteps.data.repository.DailyDayNoteRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetDayNoteUseCase(
    private val repo: DailyDayNoteRepository
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    operator fun invoke(): Flow<DailyDayNote?> =
        repo.getNote(LocalDate.now().format(fmt))
}
