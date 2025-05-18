package com.example.dailysteps.domain.usecase.tasks

import com.example.dailysteps.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryDatesUseCase(private val repo: TaskRepository) {
    operator fun invoke(): Flow<List<String>> = repo.getAllDates()
}