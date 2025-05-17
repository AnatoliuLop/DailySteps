package com.example.dailysteps.domain.usecase.tasks

import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.repository.TaskRepository

class ToggleDoneUseCase(
    private val repo: TaskRepository
) {
    suspend operator fun invoke(task: DailyTask) {
        repo.update(task.copy(done = !task.done))
    }
}
