package com.example.dailysteps.data

import com.example.dailysteps.data.local.AppDatabase
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.StepEntry
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.data.repository.*
import com.example.dailysteps.domain.usecase.daynote.GetDayNoteUseCase
import com.example.dailysteps.domain.usecase.daynote.SaveDayNoteUseCase
import com.example.dailysteps.domain.usecase.stats.GetStreakUseCase
import com.example.dailysteps.domain.usecase.stats.GetTaskStreaksUseCase
import com.example.dailysteps.domain.usecase.steps.GetStepEntryUseCase
import com.example.dailysteps.domain.usecase.steps.UpdateStepEntryUseCase
import com.example.dailysteps.domain.usecase.tasks.AddTaskUseCase
import com.example.dailysteps.domain.usecase.tasks.DeleteTaskUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import com.example.dailysteps.domain.usecase.tasks.ToggleDoneUseCase
import com.example.dailysteps.domain.usecase.tasks.UpdateTaskUseCase
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

    // === Use-cases: Tasks ===
    fun provideGetTasksUseCase()    = GetTasksUseCase(provideTaskRepository())
    fun provideAddTaskUseCase()     = AddTaskUseCase(provideTaskRepository())
    fun provideToggleDoneUseCase()  = ToggleDoneUseCase(provideTaskRepository())
    fun provideUpdateTaskUseCase()  = UpdateTaskUseCase(provideTaskRepository())
    fun provideDeleteTaskUseCase()  = DeleteTaskUseCase(provideTaskRepository())

    // === Use-cases: DayNote ===
    fun provideGetDayNoteUseCase()  = GetDayNoteUseCase(provideNoteRepo())
    fun provideSaveDayNoteUseCase() = SaveDayNoteUseCase(provideNoteRepo())

    // === Use-cases: Steps ===
    fun provideGetStepEntryUseCase()    = GetStepEntryUseCase(provideStepRepo())
    fun provideUpdateStepEntryUseCase() = UpdateStepEntryUseCase(provideStepRepo())

    // === Use-cases: Stats ===
    fun provideGetStreakUseCase()        = GetStreakUseCase(provideTaskRepository())
    fun provideGetTaskStreaksUseCase()  = GetTaskStreaksUseCase(provideDefaultRepo(), provideTaskRepository())

    // === Инициализация нового дня ===
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