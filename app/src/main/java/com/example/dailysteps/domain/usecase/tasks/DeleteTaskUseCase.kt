package com.example.dailysteps.domain.usecase.tasks

import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.repository.TaskRepository

class DeleteTaskUseCase(private val repo: TaskRepository) {
    suspend operator fun invoke(task: DailyTask) = repo.delete(task)
}

