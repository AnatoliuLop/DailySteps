package com.example.dailysteps.data.repository

import com.example.dailysteps.data.model.DefaultTask
import kotlinx.coroutines.flow.Flow

interface DefaultTaskRepository {
    fun getAll(): Flow<List<DefaultTask>>
    suspend fun insert(task: DefaultTask): Long
    suspend fun update(task: DefaultTask)
    suspend fun delete(task: DefaultTask)
}
