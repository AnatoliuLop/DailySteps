package com.example.dailysteps

import android.app.Application
import androidx.room.Room
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.data.local.AppDatabase
import com.example.dailysteps.data.preferences.PreferencesManager

class DailyStepsApp : Application() {
    override fun onCreate() {
        super.onCreate()

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
}
