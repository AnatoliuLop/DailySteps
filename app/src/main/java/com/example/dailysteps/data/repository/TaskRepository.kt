package com.example.dailysteps.data.repository

import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.DateRateEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(date: String): Flow<List<DailyTask>>
    suspend fun insertAll(tasks: List<DailyTask>)
    suspend fun insert(task: DailyTask): Long
    suspend fun update(task: DailyTask)
    suspend fun delete(task: DailyTask)
    fun getByDefaultTaskId(defaultTaskId: Int): Flow<List<DailyTask>>
    fun getAllDates(): Flow<List<String>>
    fun getCompletionRatesInPeriod(start: String, end: String): Flow<List<DateRateEntity>>
    fun existsOnDate(dateIso: String, description: String): Flow<Boolean>
    fun getCompletedInRange(fromIso: String, toIso: String): Flow<List<DailyTask>>
}
