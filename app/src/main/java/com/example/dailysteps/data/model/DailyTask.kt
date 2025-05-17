package com.example.dailysteps.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("date")]
)
data class DailyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,             // формат ISO_DATE
    val defaultTaskId: Int?,      // ссылка на шаблон, nullable для новых задач
    val category: String,
    val description: String,
    val done: Boolean = false,
    val note: String? = null      // примечание к задаче
)
