package com.example.dailysteps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dailysteps.data.model.*

@Database(
    entities = [
        DefaultTask::class,
        DailyTask::class,
        DailyDayNote::class,
        StepEntry::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun defaultTaskDao(): DefaultTaskDao
    abstract fun dailyTaskDao(): DailyTaskDao
    abstract fun dailyDayNoteDao(): DailyDayNoteDao
    abstract fun stepEntryDao(): StepEntryDao
}
