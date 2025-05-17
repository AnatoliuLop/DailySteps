package com.example.dailysteps.data.repository

import com.example.dailysteps.data.local.StepEntryDao
import com.example.dailysteps.data.model.StepEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StepEntryRepositoryImpl(
    private val dao: StepEntryDao
) : StepEntryRepository {

    override fun getEntry(date: String): Flow<StepEntry?> =
        dao.getByDate(date)

    override suspend fun insert(entry: StepEntry) = withContext(Dispatchers.IO) {
        dao.insert(entry)
    }

    override suspend fun updateActual(date: String, actual: Int) = withContext(Dispatchers.IO) {
        dao.updateActual(date, actual)
    }
}
