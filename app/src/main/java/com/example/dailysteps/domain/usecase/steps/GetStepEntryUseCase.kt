package com.example.dailysteps.domain.usecase.steps

import com.example.dailysteps.data.model.StepEntry
import com.example.dailysteps.data.repository.StepEntryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetStepEntryUseCase(
    private val repo: StepEntryRepository
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    operator fun invoke(): Flow<StepEntry?> =
        repo.getEntry(LocalDate.now().format(fmt))
}
