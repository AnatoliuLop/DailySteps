package com.example.dailysteps.data.repository

import com.example.dailysteps.data.model.DailyTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(date: String): Flow<List<DailyTask>>
    suspend fun insertAll(tasks: List<DailyTask>)
    suspend fun insert(task: DailyTask): Long
    suspend fun update(task: DailyTask)
    suspend fun delete(task: DailyTask)
    // ← объявляем новый метод
    fun getByDefaultTaskId(defaultTaskId: Int): Flow<List<DailyTask>>
}
