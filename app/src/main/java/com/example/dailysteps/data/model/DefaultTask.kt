package com.example.dailysteps.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DefaultTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val description: String
)
