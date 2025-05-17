package com.example.dailysteps.domain.usecase

import com.example.dailysteps.data.repository.StepEntryRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UpdateStepEntryUseCase(
    private val repo: StepEntryRepository
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    suspend operator fun invoke(actualCount: Int) {
        val date = LocalDate.now().format(fmt)
        repo.updateActual(date, actualCount)
    }
}
