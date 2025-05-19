package com.example.dailysteps.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyDayNote(
    @PrimaryKey val date: String,
    val note: String
)
