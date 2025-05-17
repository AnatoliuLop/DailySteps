package com.example.dailysteps.domain.usecase

import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.repository.TaskRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddTaskUseCase(
    private val repo: TaskRepository
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    suspend operator fun invoke(
        description: String,
        category: String = "general"
    ) {
        val date = LocalDate.now().format(fmt)
        repo.insert(DailyTask(
            date = date,
            defaultTaskId = null,
            category = category,
            description = description
        ))
    }
}
