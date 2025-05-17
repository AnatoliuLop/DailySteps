package com.example.dailysteps.domain.usecase

import com.example.dailysteps.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Возвращает количество дней подряд (включая сегодня),
 * когда все задачи были выполнены или задач не было.
 */
class GetStreakUseCase(
    private val repo: TaskRepository
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    operator fun invoke(): Flow<Int> = flow {
        var streak = 0
        var date = LocalDate.now()
        while (true) {
            val tasks = repo.getTasks(date.format(fmt)).first()
            val allDone = tasks.isEmpty() || tasks.all { it.done }
            if (allDone) {
                streak++
                date = date.minusDays(1)
            } else break
        }
        emit(streak)
    }
}
