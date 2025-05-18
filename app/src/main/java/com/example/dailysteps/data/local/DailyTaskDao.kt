package com.example.dailysteps.data.local

import androidx.room.*
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.DateRateEntity
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

    //History + stat
    @Query("SELECT DISTINCT date FROM DailyTask ORDER BY date DESC")
    fun getAllDates(): Flow<List<String>>

    @Query("""
  SELECT date,
         SUM(CASE WHEN done=1 THEN 1 ELSE 0 END) * 1.0 / COUNT(*) AS pct
  FROM DailyTask
  WHERE date BETWEEN :start AND :end
  GROUP BY date
""")
    fun getCompletionRatesInPeriod(start: String, end: String): Flow<List<DateRateEntity>>

    @Query("""
  SELECT EXISTS(
    SELECT 1 FROM DailyTask 
     WHERE date = :date AND description = :desc
  )
""")
    fun existsTaskOnDate(date: String, desc: String): Flow<Boolean>

    @Query("""
  SELECT * FROM DailyTask
  WHERE date BETWEEN :fromIso AND :toIso AND done = 1
""")
    fun getCompletedInRange(fromIso: String, toIso: String): Flow<List<DailyTask>>
}
