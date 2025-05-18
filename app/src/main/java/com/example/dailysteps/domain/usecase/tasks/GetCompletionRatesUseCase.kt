package com.example.dailysteps.domain.usecase.tasks

import com.example.dailysteps.data.model.DateRateEntity
import com.example.dailysteps.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCompletionRatesUseCase(private val repo: TaskRepository) {
    operator fun invoke(
        start: String,
        end: String
    ): Flow<List<DateRateEntity>> =
        repo.getCompletionRatesInPeriod(start, end)
            .map { list ->
                list.map { DateRateEntity(it.date, it.pct) }
            }
}