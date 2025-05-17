package com.example.dailysteps.data.local

import androidx.room.*
import com.example.dailysteps.data.model.DailyDayNote
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyDayNoteDao {
    @Query("SELECT * FROM DailyDayNote WHERE date = :date")
    fun getByDate(date: String): Flow<DailyDayNote?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: DailyDayNote)

    @Delete
    fun delete(note: DailyDayNote)
}
