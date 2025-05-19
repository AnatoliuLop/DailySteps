package com.example.dailysteps.data

import com.example.dailysteps.data.local.AppDatabase
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.data.repository.*
import com.example.dailysteps.domain.usecase.daynote.GetDayNoteUseCase
import com.example.dailysteps.domain.usecase.daynote.SaveDayNoteUseCase
import com.example.dailysteps.domain.usecase.stats.GetStreakUseCase
import com.example.dailysteps.domain.usecase.stats.GetTaskStreaksUseCase
import com.example.dailysteps.domain.usecase.stats.GetWeeklyCompletionUseCase
import com.example.dailysteps.domain.usecase.tasks.AddTaskUseCase
import com.example.dailysteps.domain.usecase.tasks.DeleteTaskUseCase
import com.example.dailysteps.domain.usecase.tasks.GetCompletionRatesUseCase
import com.example.dailysteps.domain.usecase.tasks.GetHistoryDatesUseCase
import com.example.dailysteps.domain.usecase.tasks.GetTasksUseCase
import com.example.dailysteps.domain.usecase.tasks.ToggleDoneUseCase
import com.example.dailysteps.domain.usecase.tasks.UpdateTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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



    fun provideGetTasksUseCase()    = GetTasksUseCase(provideTaskRepository())
    fun provideAddTaskUseCase()     = AddTaskUseCase(provideTaskRepository())
    fun provideToggleDoneUseCase()  = ToggleDoneUseCase(provideTaskRepository())
    fun provideUpdateTaskUseCase()  = UpdateTaskUseCase(provideTaskRepository())
    fun provideDeleteTaskUseCase()  = DeleteTaskUseCase(provideTaskRepository())


    fun provideGetDayNoteUseCase(): GetDayNoteUseCase =
        GetDayNoteUseCase(provideNoteRepo(), preferences)

    fun provideSaveDayNoteUseCase(): SaveDayNoteUseCase =
        SaveDayNoteUseCase(provideNoteRepo(), preferences)


    fun provideGetHistoryDatesUseCase() =
        GetHistoryDatesUseCase(provideTaskRepository())

    fun provideGetCompletionRatesUseCase() =
        GetCompletionRatesUseCase(provideTaskRepository())


    fun provideGetWeeklyCompletionUseCase() =
        GetWeeklyCompletionUseCase(provideTaskRepository())

    fun provideGetTodayCompletionUseCase(): GetCompletionRatesUseCase =
        GetCompletionRatesUseCase(provideTaskRepository())

    fun provideGetStreakUseCase() = GetStreakUseCase(provideTaskRepository(), preferences)
    fun provideGetTaskStreaksUseCase() =
        GetTaskStreaksUseCase(provideDefaultRepo(), provideTaskRepository())

    suspend fun initNewDay(date: String) {
        val defaults = provideDefaultRepo().getAll().first()
        val daily = defaults.map { dt ->
            DailyTask(date = date, defaultTaskId = dt.id, category = dt.category, description = dt.description)
        }
        provideTaskRepository().insertAll(daily)

    }



    /** Для тестов: переключиться на предыдущий день */
    suspend fun debugPreviousDay() {
        val prefs = preferences
        val isoFmt = DateTimeFormatter.ISO_DATE
        val current = prefs.lastDate.first().takeIf { it.isNotBlank() }
            ?: LocalDate.now().format(isoFmt)
        val prev = LocalDate.parse(current, isoFmt).minusDays(1).format(isoFmt)
        prefs.setLastDate(prev)
    }

    suspend fun debugNextDay() {
        val prefs = preferences
        val isoFmt = DateTimeFormatter.ISO_DATE
        val current = prefs.lastDate.first().takeIf { it.isNotBlank() }
            ?: LocalDate.now().format(isoFmt)
        val next = LocalDate.parse(current, isoFmt).plusDays(1).format(isoFmt)
        prefs.setLastDate(next)
    }

    /** Полный сброс и заново создаём «сегодня» */
    suspend fun debugReset() {
        val todayIso = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        // Чистим БД в IO
        withContext(Dispatchers.IO) {
            db.clearAllTables()
        }
        // Сбрасываем дату и инициализируем день
        preferences.setLastDate(todayIso)
        initNewDay(todayIso)
    }



}