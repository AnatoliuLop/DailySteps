package com.example.dailysteps.domain.usecase

import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetTasksUseCase(
    private val repo: TaskRepository
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    operator fun invoke(date: LocalDate): Flow<List<DailyTask>> =
        repo.getTasks(date.format(fmt))
}
