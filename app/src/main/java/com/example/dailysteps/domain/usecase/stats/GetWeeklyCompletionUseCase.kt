package com.example.dailysteps.domain.usecase.stats

import com.example.dailysteps.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class TaskWeeklyCount(val description: String, val daysDone: Int)

class GetWeeklyCompletionUseCase(private val repo: TaskRepository) {
    operator fun invoke(fromIso: String, toIso: String): Flow<List<TaskWeeklyCount>> =
        repo.getCompletedInRange(fromIso, toIso)
            .map { list ->
                list
                    .groupBy { it.description }
                    .map { (desc, tasks) ->

                        val distinctDays = tasks.map { it.date }.distinct().size
                        TaskWeeklyCount(desc, distinctDays)
                    }
                    .sortedByDescending { it.daysDone }
            }
}