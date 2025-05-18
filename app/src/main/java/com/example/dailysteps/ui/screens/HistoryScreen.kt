package com.example.dailysteps.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.DateRateEntity
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.ui.components.StandardTopBar
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    calendarDates:    StateFlow<List<String>>,
    historyRates:     StateFlow<List<DateRateEntity>>,
    tasksForDate:     StateFlow<List<DailyTask>>,
    dayNoteForDate:   StateFlow<String>,
    selectedDate:     StateFlow<String?>,
    onDaySelected:    (String) -> Unit,
    onClearSelection: () -> Unit,
    onBack:           () -> Unit,
    onSettings:       () -> Unit,
    prefs: PreferencesManager      // передаём prefs
) {
    val fmt = DateTimeFormatter.ISO_DATE

    // Собираем потоки
    val dates       by calendarDates.collectAsState()
    val rates       by historyRates.collectAsState()
    val tasks       by tasksForDate.collectAsState()
    val note        by dayNoteForDate.collectAsState()
    val selIso      by selectedDate.collectAsState()
    val currIso     by prefs.lastDate
        .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(fmt) }
        .collectAsState(initial = LocalDate.now().format(fmt))

    Scaffold(
        topBar = {
            StandardTopBar("History", onBack, onSettings)
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dates) { iso ->
                    val pct = rates.find { it.date == iso }?.pct ?: 0.0
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onDaySelected(iso) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(iso)
                        if (iso == currIso) {
                            Text("In process")
                        } else {
                            Text("${(pct * 100).toInt()}%")
                        }
                    }
                }
            }
        }

        // Диалог с деталями — теперь прокручиваемый
        if (selIso != null) {
            AlertDialog(
                onDismissRequest = onClearSelection,
                title = { Text("Details for $selIso") },
                text = {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)             // ограничиваем высоту
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Note:")
                        Text(note.ifBlank { "<no note>" })

                        Divider()

                        Text("Tasks:")
                        tasks.forEach { task ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(task.description)
                                Text(if (task.done) "✅" else "❌")
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onClearSelection) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
