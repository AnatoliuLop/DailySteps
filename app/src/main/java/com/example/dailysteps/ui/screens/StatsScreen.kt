package com.example.dailysteps.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow

import com.example.dailysteps.ui.viewmodel.TaskStreak

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    percentDone: Flow<Float>,
    streak: Flow<Int>,
    taskStreaks: Flow<List<TaskStreak>>,
    onBack: () -> Unit
) {
    val pct by percentDone.collectAsState(initial = 0f)
    val str by streak.collectAsState(initial = 0)
    val streakList by taskStreaks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1) Daily Completion
            Text("Daily Completion", fontSize = 20.sp)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    drawArc(
                        color = Color(0xFF4CAF50),
                        startAngle = -90f,
                        sweepAngle = pct * 360f,
                        useCenter = true
                    )
                    drawArc(
                        color = Color.LightGray,
                        startAngle = -90f + pct * 360f,
                        sweepAngle = (1 - pct) * 360f,
                        useCenter = true
                    )
                }
                Text("${(pct * 100).toInt()}%", fontSize = 24.sp)
            }

            Divider()

            // 2) Task Streaks
            Text("Task Streaks", fontSize = 20.sp)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(streakList) { ts ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(ts.name)
                        Text("${ts.days} days")
                    }
                }
            }
        }
    }
}
