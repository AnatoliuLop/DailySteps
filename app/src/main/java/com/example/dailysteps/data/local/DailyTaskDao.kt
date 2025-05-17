package com.example.dailysteps.data.local

import androidx.room.*
import com.example.dailysteps.data.model.DailyTask
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTaskDao {
    @Query("SELECT * FROM DailyTask WHERE date = :date")
    fun getByDate(date: String): Flow<List<DailyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tasks: List<DailyTask>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: DailyTask): Long

    @Update
    fun update(task: DailyTask)

    @Delete
    fun delete(task: DailyTask)

    @Query("SELECT * FROM DailyTask WHERE defaultTaskId = :defaultTaskId")
    fun getByDefaultTaskId(defaultTaskId: Int): Flow<List<DailyTask>>
}
