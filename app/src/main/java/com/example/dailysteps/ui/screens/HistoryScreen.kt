package com.example.dailysteps.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onDaySelected: (LocalDate) -> Unit,
    onBack: () -> Unit
) {
    val today = LocalDate.now()
    val month = today.month
    val year = today.year
    val daysInMonth = today.lengthOfMonth()
    val fmt = DateTimeFormatter.ofPattern("MMM yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${month.name.lowercase().capitalize()} $year") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val cols = 7
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            for (weekStart in 1..daysInMonth step cols) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    (weekStart until (weekStart + cols)).forEach { day ->
                        if (day <= daysInMonth) {
                            val date = LocalDate.of(year, month, day)
                            // Цвет по логике (заглушка: белый)
                            val bg = if (date == today) Color.Yellow else Color.LightGray
                            Canvas(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { onDaySelected(date) }
                            ) {
                                drawCircle(color = bg)
                                drawContext.canvas.nativeCanvas.drawText(
                                    day.toString(),
                                    size.width / 2,
                                    size.height / 2 + 8f,
                                    android.graphics.Paint().apply {
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        textSize = 24f
                                        color = android.graphics.Color.BLACK
                                    }
                                )
                            }
                        } else {
                            Spacer(Modifier.size(36.dp))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
