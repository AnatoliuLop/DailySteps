package com.example.dailysteps.data.repository

import com.example.dailysteps.data.model.DailyDayNote
import kotlinx.coroutines.flow.Flow

interface DailyDayNoteRepository {
    fun getNote(date: String): Flow<DailyDayNote?>
    suspend fun insert(note: DailyDayNote)
    suspend fun delete(note: DailyDayNote)
    suspend fun saveNote(note: DailyDayNote)
}
