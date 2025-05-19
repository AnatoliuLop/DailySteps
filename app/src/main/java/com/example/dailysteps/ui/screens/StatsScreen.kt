package com.example.dailysteps.ui.screens


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailysteps.domain.usecase.stats.TaskWeeklyCount
import com.example.dailysteps.ui.viewmodel.TaskStreak
import kotlinx.coroutines.flow.Flow
import com.example.dailysteps.ui.components.StandardTopBar
import androidx.compose.ui.text.style.TextAlign
import com.example.dailysteps.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    weeklyStats: Flow<List<TaskWeeklyCount>>,
    percentDone: Flow<Float>,
    streak: Flow<Int>,
    taskStreaks: Flow<List<TaskStreak>>,
    onBack: () -> Unit,
    onSettings: () -> Unit
) {
    val pct by percentDone.collectAsState(initial = 0f)
    val total by streak.collectAsState(initial = 0)
    val streakList by taskStreaks.collectAsState(initial = emptyList())
    val weekly by weeklyStats.collectAsState(initial = emptyList())

    // 1) Выбор цвета дуги
    val arcColor = when {
        pct == 0f              -> Color.LightGray
        pct < 0.5f             -> Color.Red
        pct < 0.75f            -> Color(0xFFFFA000)
        else                   -> Color(0xFF4CAF50)
    }

    // 2) Мотивация без раскраски, увеличенный размер
    val msg = when {
        pct == 0f              -> stringResource(R.string.msg_zero)
        pct < 0.5f             -> stringResource(R.string.msg_low)
        pct < 0.75f            -> stringResource(R.string.msg_mid)
        else                   -> stringResource(R.string.msg_high)
    }

    Scaffold(
        topBar = { StandardTopBar(stringResource(R.string.statistics), onBack, onSettings) }
    ) { padding ->
        // добавляем вертикальный скролл
        val scroll = rememberScrollState()
        Column(
            Modifier
                .verticalScroll(scroll)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Средний процент ---
            Text(
                text = stringResource(R.string.todays_completion_rate),
                fontSize = 20.sp
            )
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    drawArc(
                        color = arcColor,
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
                Text("${(pct * 100).toInt()}%", fontSize = 28.sp)
            }
            Text(msg, fontSize = 18.sp)

            Divider()

            // --- Weekly completion ---
            Text(
                text = stringResource(R.string.completed_last_week),
                fontSize = 20.sp
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                weekly
                    .sortedByDescending { it.daysDone }
                    .take(4)
                    .forEach { w ->
                        Text(
                            text = w.description,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        LinearProgressIndicator(
                            progress = (w.daysDone / 7f).coerceIn(0f, 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Text(
                            text = "${w.daysDone}/7",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 4.dp),
                            textAlign = TextAlign.End
                        )
                    }
            }
        }
    }
}