package com.example.dailysteps

import android.app.Application
import androidx.room.Room
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.data.local.AppDatabase
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.work.DailyRolloverWorker
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class DailyStepsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleDailyRollover()
        // 1. Создаём Room-базу
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "daily_steps.db"
        )
            .fallbackToDestructiveMigration() // на время разработки
            .build()

        // 2. Создаём PreferencesManager
        val prefs = PreferencesManager(applicationContext)

        // 3. Инициализируем ServiceLocator
        ServiceLocator.init(db, prefs)
    }

    private fun scheduleDailyRollover() {
        // Вычисляем задержку до ближайшего 00:01
        val now   = LocalDateTime.now()
        val next  = now.toLocalDate()
            .atTime(LocalTime.of(0,1))
            .plusDays(if (now.toLocalTime() >= LocalTime.of(0,1)) 1 else 0)
        val initialDelay = Duration.between(now, next).toMillis()

        val work = PeriodicWorkRequestBuilder<DailyRolloverWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1, TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "daily_rollover",
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
    }
}
