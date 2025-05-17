package com.example.dailysteps.domain.usecase

import com.example.dailysteps.data.model.DefaultTask
import com.example.dailysteps.data.repository.DefaultTaskRepository
import com.example.dailysteps.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Доменный DTO (переименован, чтобы не путать с UI-моделью)
data class DomainTaskStreak(val name: String, val days: Int)

class GetTaskStreaksUseCase(
    private val defaultRepo: DefaultTaskRepository,
    private val taskRepo: TaskRepository
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    operator fun invoke(): Flow<List<DomainTaskStreak>> {
        return defaultRepo.getAll().flatMapLatest { defaults ->
            combine(defaults.map { dt: DefaultTask ->
                taskRepo.getByDefaultTaskId(dt.id)
                    .map { dailyList ->
                        // сортируем по дате и считаем подряд выполненные с конца
                        val sorted = dailyList.sortedBy { LocalDate.parse(it.date, fmt) }
                        var cnt = 0
                        for (t in sorted.reversed()) {
                            if (t.done) cnt++ else break
                        }
                        DomainTaskStreak(dt.description, cnt)
                    }
            }) { arrayOf ->
                arrayOf.toList()
            }
        }
    }
}
