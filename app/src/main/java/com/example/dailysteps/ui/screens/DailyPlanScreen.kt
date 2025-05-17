package com.example.dailysteps.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.ui.components.StandardTopBar
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPlanScreen(
    tasks: StateFlow<List<DailyTask>>,
    onAdd: (String) -> Unit,
    onNoteChange: (DailyTask, String) -> Unit,
    onEdit: (DailyTask, String) -> Unit,      // ← новый
    onDelete: (DailyTask) -> Unit,            // ← новый
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSettings: () -> Unit         // ← новый параметр
) {
    val list by tasks.collectAsState()
    var newText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            StandardTopBar(
                title = "Daily Plan",
                onBack = onBack,
                onSettings = onSettings
            )
        },
        bottomBar = {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Review")
            }
        }
    )  { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list, key = { it.id }) { task ->
                    var isEditing by remember(task.id) { mutableStateOf(false) }
                    var draftDesc by remember(task.id) { mutableStateOf(task.description) }
                    var noteText by remember(task.id) { mutableStateOf(task.note ?: "") }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // 1) название / inline-редактор
                        if (isEditing) {
                            OutlinedTextField(
                                value = draftDesc,
                                onValueChange = { draftDesc = it },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        onEdit(task, draftDesc.trim())
                                        isEditing = false
                                    }) {
                                        Icon(Icons.Default.Check, "Save")
                                    }
                                }
                            )
                        } else {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(task.description)
                            }
                        }

                        // 2) поле заметки
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = {
                                noteText = it
                                onNoteChange(task, it)
                            },
                            placeholder = { Text("Note") },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(TextFieldDefaults.MinHeight),
                            maxLines = Int.MAX_VALUE
                        )

                        // 3) кнопки
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, "Edit")
                        }
                        IconButton(onClick = { onDelete(task) }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = newText,
                onValueChange = { newText = it },
                placeholder = { Text("New task") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (newText.isNotBlank()) {
                        onAdd(newText.trim())
                        newText = ""
                    }
                },
                Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Text("Add")
            }
        }
    }
}


