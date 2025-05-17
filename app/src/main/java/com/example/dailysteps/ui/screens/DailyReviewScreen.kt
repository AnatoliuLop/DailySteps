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
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReviewScreen(
    tasks: StateFlow<List<DailyTask>>,
    dayNote: StateFlow<DailyDayNote?>,
    onToggle: (DailyTask) -> Unit,
    onSaveNote: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val list by tasks.collectAsState()
    val note by dayNote.collectAsState()
    var editNote by remember { mutableStateOf(note?.note ?: "") }
    var dialogOpen by remember { mutableStateOf(false) }

    if (dialogOpen) {
        AlertDialog(
            onDismissRequest = { dialogOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    onSaveNote(editNote)
                    dialogOpen = false
                }) { Text("Save") }
            },
            text = {
                OutlinedTextField(
                    value = editNote,
                    onValueChange = { editNote = it },
                    label = { Text("Day note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Day") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { dialogOpen = true }) {
                        Icon(Icons.Default.NoteAdd, contentDescription = "Note")
                    }
                }
            )
        },
        bottomBar = {
            Button(onClick = onNext, Modifier.fillMaxWidth().padding(16.dp)) {
                Text("History")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(list) { task ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = task.done, onCheckedChange = { onToggle(task) })
                    Text(task.description)
                }
            }
        }
    }
}
