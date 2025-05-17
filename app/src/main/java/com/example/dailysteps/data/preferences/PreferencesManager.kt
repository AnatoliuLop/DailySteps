package com.example.dailysteps.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit

private val Context.dataStore by preferencesDataStore("user_prefs")

class PreferencesManager(private val context: Context) {
    companion object {
        private val DARK_KEY   = booleanPreferencesKey("dark_theme")
        private val LOCALE_KEY = stringPreferencesKey("locale")
        private val STEP_GOAL_KEY = intPreferencesKey("step_goal")
        private val LAST_DATE_KEY = stringPreferencesKey("last_date")
    }

    // темы
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { it[DARK_KEY] ?: false }

    suspend fun setDarkTheme(value: Boolean) = context.dataStore.edit {
        it[DARK_KEY] = value
    }

    // локаль
    val locale: Flow<String> = context.dataStore.data
        .map { it[LOCALE_KEY] ?: "en" }

    suspend fun setLocale(value: String) = context.dataStore.edit {
        it[LOCALE_KEY] = value
    }

    // цель шагов
    val stepGoal: Flow<Int> = context.dataStore.data
        .map { it[STEP_GOAL_KEY] ?: 10000 } // по умолчанию 10000 шагов

    suspend fun setStepGoal(value: Int) = context.dataStore.edit {
        it[STEP_GOAL_KEY] = value
    }

    // последний обработанный день
    val lastDate: Flow<String> = context.dataStore.data
        .map { it[LAST_DATE_KEY] ?: "" }

    suspend fun setLastDate(value: String) = context.dataStore.edit {
        it[LAST_DATE_KEY] = value
    }
}
