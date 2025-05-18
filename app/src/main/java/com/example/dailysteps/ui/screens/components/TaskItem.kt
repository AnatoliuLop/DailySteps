package com.example.dailysteps.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailysteps.data.model.DailyTask
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: DailyTask,
    onNoteChange: (DailyTask, String) -> Unit,
    onEdit:       (DailyTask, String) -> Unit,
    onDelete:     (DailyTask)        -> Unit
) {
    var isEditing by remember(task.id) { mutableStateOf(false) }
    var draftDesc by remember(task.id) { mutableStateOf(task.description) }
    val focusManager = LocalFocusManager.current
    // локальный state для заметки
    var noteText by remember(task.id) { mutableStateOf(task.note.orEmpty()) }

    // синхронизируемся с тем, что хранится в БД, когда task.note меняется «извне»
    LaunchedEffect(task.note) {
        noteText = task.note.orEmpty()
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- Description Edit (без изменений) ---
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
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        } else {
            Box(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(task.description)
            }
        }

        // --- Note Edit: сохраняем только при уходе фокуса или на Done ---
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = { Text("Note") },
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(TextFieldDefaults.MinHeight)
                .onFocusChanged { focusState ->
                    // когда потеряли фокус — сохраняем
                    if (!focusState.isFocused) {
                        onNoteChange(task, noteText)
                    }
                },
            maxLines = Int.MAX_VALUE,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                // при нажатии Done тоже теряем фокус, что сработает onFocusChanged
                focusManager.clearFocus()
            })
        )

        IconButton(onClick = { isEditing = true }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { onDelete(task) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}