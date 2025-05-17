package com.example.dailysteps.data.repository

import com.example.dailysteps.data.local.DefaultTaskDao
import com.example.dailysteps.data.model.DefaultTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DefaultTaskRepositoryImpl(
    private val dao: DefaultTaskDao
) : DefaultTaskRepository {

    override fun getAll(): Flow<List<DefaultTask>> =
        dao.getAll()

    override suspend fun insert(task: DefaultTask) = withContext(Dispatchers.IO) {
        dao.insert(task)
    }

    override suspend fun update(task: DefaultTask) = withContext(Dispatchers.IO) {
        dao.update(task)
    }

    override suspend fun delete(task: DefaultTask) = withContext(Dispatchers.IO) {
        dao.delete(task)
    }
}
