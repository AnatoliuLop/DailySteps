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


        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "daily_steps.db"
        )
            .fallbackToDestructiveMigration()
            .build()


        val prefs = PreferencesManager(applicationContext)


        ServiceLocator.init(db, prefs)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper.createNotificationChannel(this)
        }


        scheduleDailyRollover()


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

        schedulePeriodicReminder("morning", 8, 8, "reminder_morning")

        schedulePeriodicReminder("midday", 12, 12, "reminder_midday")

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
