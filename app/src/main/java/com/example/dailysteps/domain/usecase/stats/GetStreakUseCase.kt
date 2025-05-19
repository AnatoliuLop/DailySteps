package com.example.dailysteps.domain.usecase.stats

import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetStreakUseCase(
    private val repo: TaskRepository,
    private val prefs: PreferencesManager
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    /**
     * Считает количество предыдущих дней подряд (без сегодняшнего),
     * когда все задачи были выполнены (и задач не было).
     */
    operator fun invoke(): Flow<Int> =
        prefs.lastDate
            .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(fmt) }
            .map { LocalDate.parse(it, fmt) }
            .flatMapLatest { today ->
                flow {
                    var streak = 0
                    var date = today.minusDays(1)
                    while (true) {
                        val tasks = repo.getTasks(date.format(fmt)).first()
                        // если задач нет, считаем день «просроченным»?
                        // или менять логику — up to you
                        val allDone = tasks.isNotEmpty() && tasks.all { it.done }
                        if (allDone) {
                            streak++
                            date = date.minusDays(1)
                        } else {
                            break
                        }
                    }
                    emit(streak)
                }
            }
}
