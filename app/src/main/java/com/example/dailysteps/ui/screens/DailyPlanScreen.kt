package com.example.dailysteps.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dailysteps.data.model.DailyTask
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPlanScreen(
    tasks: StateFlow<List<DailyTask>>,
    onAdd: (String) -> Unit,
    onToggle: (DailyTask) -> Unit,
    onNoteChange: (DailyTask, String) -> Unit,    // новый callback
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val list by tasks.collectAsState()
    var newText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Plan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(onClick = onNext, Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
                Text("Review")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list, key = { it.id }) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,       // центрируем всех детей по высоте строки
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1) Чекбокс
                        Checkbox(
                            checked = task.done,
                            onCheckedChange = { onToggle(task) }
                        )

                        // 2) Название задачи — cell шириной 50%
                        Box(
                            modifier = Modifier
                                .weight(1f)              // половина свободного места
                                .fillMaxHeight(),        // растягивается на всю высоту строки
                            contentAlignment = Alignment.CenterStart  // выравнивание текста по центру ячейки
                        ) {
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = Int.MAX_VALUE,
                                overflow = TextOverflow.Visible
                            )
                        }

                        // 3) Заметка — вторая половина
                        var noteText by remember(task.id) { mutableStateOf(task.note ?: "") }
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = {
                                noteText = it
                                onNoteChange(task, it)
                            },
                            placeholder = { Text("Note") },
                            modifier = Modifier
                                .weight(1f)              // половина свободного места
                                .defaultMinSize(
                                    minHeight = TextFieldDefaults.MinHeight
                                ),                       // минимальная высота
                            maxLines = Int.MAX_VALUE,  // не ограничиваем строки
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Default
                            )
                        )
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

