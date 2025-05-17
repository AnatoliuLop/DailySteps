package com.example.dailysteps.data.repository

import com.example.dailysteps.data.local.DailyDayNoteDao
import com.example.dailysteps.data.model.DailyDayNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DailyDayNoteRepositoryImpl(
    private val dao: DailyDayNoteDao
) : DailyDayNoteRepository {

    override fun getNote(date: String): Flow<DailyDayNote?> =
        dao.getByDate(date)

    override suspend fun insert(note: DailyDayNote) = withContext(Dispatchers.IO) {
        dao.insert(note)
    }

    override suspend fun delete(note: DailyDayNote) = withContext(Dispatchers.IO) {
        dao.delete(note)
    }
    override suspend fun saveNote(note: DailyDayNote) =
        dao.insert(note)
}
