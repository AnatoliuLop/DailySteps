package com.example.dailysteps.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("date")]
)
data class DailyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val defaultTaskId: Int?,
    val category: String,
    val description: String,
    val done: Boolean = false,
    val note: String? = null
)
