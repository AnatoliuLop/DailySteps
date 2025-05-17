package com.example.dailysteps.data.repository

import com.example.dailysteps.data.model.StepEntry
import kotlinx.coroutines.flow.Flow

interface StepEntryRepository {
    fun getEntry(date: String): Flow<StepEntry?>
    suspend fun insert(entry: StepEntry)
    suspend fun updateActual(date: String, actual: Int)
}
