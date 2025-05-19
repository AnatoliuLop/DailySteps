package com.example.dailysteps.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.dailysteps.R
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
    val keyboardController = LocalSoftwareKeyboardController.current
    // Сохраняем состояние скролла списка между пересозданиями
    val listState: LazyListState = rememberLazyListState()
    Scaffold(
        topBar = {
            StandardTopBar(stringResource(R.string.review_day), onBack, onSettings)
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                OutlinedTextField(
                    value = note,
                    onValueChange = onSaveNote,
                    placeholder = { Text(stringResource(R.string.daily_note)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onCompleteDay, Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.done))
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
            collapseLabel = stringResource(R.string.show_all_tasks)
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
                title = { Text(stringResource(R.string.daily_summary)) },
                text = { Text(msg!!) },
                confirmButton = {
                    TextButton(onClick = onDismissCompletion) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }
    }
}

