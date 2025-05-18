package com.example.dailysteps.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.data.model.DailyDayNote
import com.example.dailysteps.ui.components.StandardTopBar
import com.example.dailysteps.ui.screens.components.CollapsibleList
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReviewScreen(
    tasks: StateFlow<List<DailyTask>>,
    dayNote: StateFlow<String>,
    onToggle: (DailyTask) -> Unit,
    onSaveNote: (String) -> Unit,
    onCompleteDay: () -> Unit,
    onDismissCompletion: () -> Unit,
    completionMessage: StateFlow<String?>,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onNext: () -> Unit
) {
    val list by tasks.collectAsState()
    val note by dayNote.collectAsState()
    val msg by completionMessage.collectAsState()

    Scaffold(
        topBar = {
            StandardTopBar("Review Day", onBack, onSettings)
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                OutlinedTextField(
                    value = note,
                    onValueChange = onSaveNote,
                    placeholder = { Text("Заметка на день") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onCompleteDay, Modifier.fillMaxWidth()) {
                    Text("Hotovo")
                }
            }
        }
    ) { padding ->
        CollapsibleList(
            items = list,
            threshold = 6,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            collapseLabel = "Показать все задания"
        ) { task ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.done,
                    onCheckedChange = { onToggle(task) }
                )
                Text(task.description, Modifier.padding(start = 8.dp))
            }
        }

        if (msg != null) {
            AlertDialog(
                onDismissRequest = onDismissCompletion,
                title = { Text("Итоги дня") },
                text = { Text(msg!!) },
                confirmButton = {
                    TextButton(onClick = onDismissCompletion) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

