package com.example.dailysteps.ui.screens


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailysteps.ui.viewmodel.TaskStreak
import kotlinx.coroutines.flow.Flow
import com.example.dailysteps.ui.components.StandardTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    percentDone: Flow<Float>,               // 0f…1f
    streak: Flow<Int>,
    taskStreaks: Flow<List<TaskStreak>>,    // name + days
    onBack: () -> Unit,
    onSettings: () -> Unit
) {
    val pct by percentDone.collectAsState(initial = 0f)
    val total by streak.collectAsState(0)
    val streakList by taskStreaks.collectAsState(initial = emptyList())

    // Мотивация + цвет
    val (msg, color) = when {
        pct < 0.5f  -> "Мы верим в тебя, просто старайся лучше."        to Color.Red
        pct < 0.75f -> "Супер, но всегда можно следовать плану лучше." to Color(0xFFFFA000)
        else        -> "Ты хорошо идёшь, теперь можно ставить цели побольше." to Color(0xFF4CAF50)
    }

    Scaffold(
        topBar = {
            StandardTopBar("Statistics", onBack, onSettings)
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1) Средний процент
            Text("Средний процент выполнения сегодня", fontSize = 18.sp)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    drawArc(
                        color = Color(0xFF4CAF50),
                        startAngle = -90f,
                        sweepAngle = pct * 360f,
                        useCenter = true
                    )
                    drawArc(
                        color = Color.LightGray,
                        startAngle = -90f + pct * 360f,
                        sweepAngle = (1f - pct) * 360f,
                        useCenter = true
                    )
                }
                Text("${(pct * 100).toInt()}%", fontSize = 24.sp)
            }
            Text(msg, color = color, fontSize = 16.sp)

            Divider()

            // 2) Стрики задач
            Text("Стрики задач (дней подряд)", fontSize = 18.sp)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(streakList) { ts ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ts.name, Modifier.weight(1f))
                        Text("${ts.days} дн.")
                    }
                }
            }
        }
    }
}
