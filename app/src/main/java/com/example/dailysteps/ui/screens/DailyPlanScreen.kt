package com.example.dailysteps.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dailysteps.R
import com.example.dailysteps.data.model.DailyTask
import com.example.dailysteps.ui.components.StandardTopBar
import com.example.dailysteps.ui.screens.components.CollapsibleList
import com.example.dailysteps.ui.screens.components.TaskItem
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPlanScreen(
    tasks: StateFlow<List<DailyTask>>,
    error: SharedFlow<String>,
    onAdd: (String) -> Unit,
    onNoteChange: (DailyTask, String) -> Unit,
    onEdit: (DailyTask, String) -> Unit,
    onDelete: (DailyTask) -> Unit,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onNext: () -> Unit
) {
    val list by tasks.collectAsState()
    var newText by rememberSaveable  { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(error) {
        error.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            StandardTopBar(stringResource(R.string.daily_plan), onBack, onSettings)
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newText,
                        onValueChange = { newText = it },
                        placeholder = { Text(stringResource(R.string.new_task)) },
                        modifier = Modifier.weight(1f),
                         keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                         ),
                        keyboardActions = KeyboardActions(
                            onDone = {

                                if (newText.isNotBlank()) {
                                    onAdd(newText.trim())
                                    newText = ""
                                }
                                keyboardController?.hide()
                            }
                        )
                    )
                    Button(onClick = {
                        if (newText.isNotBlank()) {
                            onAdd(newText.trim())
                            newText = ""
                        }

                        keyboardController?.hide()
                    }) {
                        Text(stringResource(R.string.add))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onNext, Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.review))
                }
            }
        }
    ) { padding ->

        val listState = rememberSaveable(saver = LazyListState.Saver) {
            LazyListState()
        }
        CollapsibleList(
            items = list,
            threshold = 4,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            collapseLabel = stringResource(R.string.show_all_tasks),
            listState = listState
        ) { task ->
            Column {
                TaskItem(
                    task         = task,
                    onNoteChange = onNoteChange,
                    onEdit       = onEdit,
                    onDelete     = onDelete
                )
                Divider(Modifier.padding(vertical = 4.dp))
            }
        }
    }
}




