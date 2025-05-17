package com.example.dailysteps.data.local

import androidx.room.*
import com.example.dailysteps.data.model.StepEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface StepEntryDao {
    @Query("SELECT * FROM StepEntry WHERE date = :date")
    fun getByDate(date: String): Flow<StepEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: StepEntry)

    @Query("UPDATE StepEntry SET actual = :actual WHERE date = :date")
    fun updateActual(date: String, actual: Int)
}
