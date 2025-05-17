package com.example.dailysteps.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StepEntry(
    @PrimaryKey val date: String, // ISO_DATE
    val goal: Int,                // цель шагов
    val actual: Int = 0           // пройдено шагов
)
