package com.example.dailysteps.data.local

import androidx.room.*
import com.example.dailysteps.data.model.DefaultTask
import kotlinx.coroutines.flow.Flow

@Dao
interface DefaultTaskDao {
    @Query("SELECT * FROM DefaultTask")
    fun getAll(): Flow<List<DefaultTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: DefaultTask): Long

    @Update
    fun update(task: DefaultTask)

    @Delete
    fun delete(task: DefaultTask)
}
