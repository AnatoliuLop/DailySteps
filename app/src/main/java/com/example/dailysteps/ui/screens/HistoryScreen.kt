package com.example.dailysteps.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailysteps.data.model.DateRateEntity
import com.example.dailysteps.ui.components.StandardTopBar
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    historyRates: StateFlow<List<DateRateEntity>>,
    onDaySelected: (String) -> Unit,
    onBack: () -> Unit,
    onSettings: () -> Unit
) {
    val list by historyRates.collectAsState()
    val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

    Scaffold(
        topBar = {
            StandardTopBar("History", onBack, onSettings)
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(list) { entry ->
                // Если это сегодня и pct<1 → в процессе
                val label = if (entry.date == today && entry.pct < 1.0) {
                    "В процессе"
                } else {
                    "${(entry.pct * 100).toInt()} %"
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onDaySelected(entry.date) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(entry.date)
                    Text(label)
                }
            }
        }
    }
}
