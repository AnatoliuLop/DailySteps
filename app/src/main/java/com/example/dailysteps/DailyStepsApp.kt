// app/src/main/java/com/example/dailysteps/DailyStepsApp.kt
package com.example.dailysteps

import android.app.Application
import android.os.Build
import androidx.room.Room
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.data.local.AppDatabase
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.notifications.NotificationHelper
import com.example.dailysteps.work.DailyRolloverWorker
import com.example.dailysteps.work.ReminderWorker
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class DailyStepsApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Создаём Room-базу
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "daily_steps.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // 2. Создаём PreferencesManager
        val prefs = PreferencesManager(applicationContext)

        // 3. Инициализируем ServiceLocator
        ServiceLocator.init(db, prefs)

        // 4. Создаём канал уведомлений (только на Android O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper.createNotificationChannel(this)
        }

        // 5. Ежесуточный rollover
        scheduleDailyRollover()

        // 6. Три напоминания: утро, день, вечер
        scheduleDailyReminders()
    }

    private fun scheduleDailyRollover() {
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

    private fun scheduleDailyReminders() {
        // Утро в 8:00
        schedulePeriodicReminder("morning", 11, 40, "reminder_morning")
        // День в 12:00
        schedulePeriodicReminder("midday", 12, 12, "reminder_midday")
        // Вечер в 20:00
        schedulePeriodicReminder("evening", 20, 20, "reminder_evening")
    }

    private fun schedulePeriodicReminder(
        type: String,
        hour: Int,
        minute: Int,
        uniqueWorkName: String
    ) {
        val now = LocalDateTime.now()
        var next = now
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)
        if (now >= next) next = next.plusDays(1)

        val initialDelay = Duration.between(now, next).toMillis()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(ReminderWorker.makeInputData(type))
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}
