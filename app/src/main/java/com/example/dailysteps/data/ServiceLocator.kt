package com.example.dailysteps.data

import com.example.dailysteps.data.local.AppDatabase
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.StepEntry
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.data.repository.*
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ServiceLocator {
    private lateinit var db: AppDatabase
    lateinit var preferences: PreferencesManager

    fun init(database: AppDatabase, prefs: PreferencesManager) {
        db = database
        preferences = prefs
    }

    fun provideTaskRepository() = TaskRepositoryImpl(db.dailyTaskDao())
    fun provideDefaultRepo() = DefaultTaskRepositoryImpl(db.defaultTaskDao())
    fun provideNoteRepo() = DailyDayNoteRepositoryImpl(db.dailyDayNoteDao())
    fun provideStepRepo() = StepEntryRepositoryImpl(db.stepEntryDao())

    // Создаём новый день: копируем шаблоны + шаги
    suspend fun initNewDay(date: String) {
        val defaults = provideDefaultRepo().getAll().first()
        val daily = defaults.map { dt ->
            DailyTask(date = date, defaultTaskId = dt.id, category = dt.category, description = dt.description)
        }
        provideTaskRepository().insertAll(daily)
        val goal = preferences.stepGoal.first()
        provideStepRepo().insert(StepEntry(date = date, goal = goal))
    }
}