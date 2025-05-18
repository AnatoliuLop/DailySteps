package com.example.dailysteps.data.repository

import com.example.dailysteps.data.local.DailyTaskDao
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.DateRateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TaskRepositoryImpl(
    private val dao: DailyTaskDao
) : TaskRepository {

    override fun getTasks(date: String): Flow<List<DailyTask>> =
        dao.getByDate(date)

    override suspend fun insertAll(tasks: List<DailyTask>) = withContext(Dispatchers.IO) {
        dao.insertAll(tasks)
    }

    override suspend fun insert(task: DailyTask) = withContext(Dispatchers.IO) {
        dao.insert(task)
    }

    override suspend fun update(task: DailyTask) = withContext(Dispatchers.IO) {
        dao.update(task)
    }

    override suspend fun delete(task: DailyTask) = withContext(Dispatchers.IO) {
        dao.delete(task)
    }
    override fun getByDefaultTaskId(defaultTaskId: Int): Flow<List<DailyTask>> =
        dao.getByDefaultTaskId(defaultTaskId)

    override fun getAllDates(): Flow<List<String>> =
        dao.getAllDates()

    override fun getCompletionRatesInPeriod(
        start: String,
        end: String
    ): Flow<List<DateRateEntity>> =
        dao.getCompletionRatesInPeriod(start, end)

    override fun existsOnDate(dateIso: String, description: String) =
        dao.existsTaskOnDate(dateIso, description)

    override fun getCompletedInRange(fromIso: String, toIso: String): Flow<List<DailyTask>> =
        dao.getCompletedInRange(fromIso, toIso)
}
