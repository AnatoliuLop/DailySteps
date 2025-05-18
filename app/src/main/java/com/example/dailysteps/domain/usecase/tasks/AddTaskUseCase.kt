// app/src/main/java/com/example/dailysteps/domain/usecase/tasks/AddTaskUseCase.kt
package com.example.dailysteps.domain.usecase.tasks

import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddTaskUseCase(private val repo: TaskRepository) {
    private val fmt = DateTimeFormatter.ISO_DATE

    /**
     * @param description  текст задачи
     * @param category     категория
     * @param dateIso      дата в формате ISO (по умолчанию — сегодня)
     * @param defaultTaskId  id шаблона задачи (0, если это "ручная" задача)
     */
    suspend operator fun invoke(
        description: String,
        category: String = "general",
        dateIso: String = LocalDate.now().format(fmt),
        defaultTaskId: Int = 0
    ) {
        val exists = repo.existsOnDate(dateIso, description).first()
        if (exists) throw DuplicateTaskException()
        val task = DailyTask(
            date = dateIso,
            defaultTaskId = defaultTaskId,
            category = category,
            description = description
        )
        repo.insert(task)
    }
}