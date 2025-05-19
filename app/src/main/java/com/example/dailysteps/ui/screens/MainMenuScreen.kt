
package com.example.dailysteps.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.dailysteps.R
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    streak: StateFlow<Int>,
    onNavigate: (String) -> Unit,
    onSettings: () -> Unit,
    onDebugPrevDay: () -> Unit,
    onDebugNextDay: () -> Unit,
    onDebugReset: () -> Unit,
    onRunRolloverNow: () -> Unit         // ← новый коллбэк
) {
    val prefs = ServiceLocator.preferences
    val scope = rememberCoroutineScope()

    val currentDate by prefs.lastDate
        .map { it.takeIf(String::isNotBlank) ?: LocalDate.now().format(DateTimeFormatter.ISO_DATE) }
        .collectAsState(initial = LocalDate.now().format(DateTimeFormatter.ISO_DATE))

    val streakVal by streak.collectAsState()

    var devMenuExpanded by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsDialog(
            currentLocale = prefs.locale.collectAsState(initial = "en").value,
            onLocaleChange = { newLoc -> scope.launch { prefs.setLocale(newLoc) } },
            currentTheme = prefs.isDarkTheme.collectAsState(initial = false).value,
            onThemeChange = { dark -> scope.launch { prefs.setDarkTheme(dark) } },
            onDismiss = { showSettings = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        }
    ) { padding ->
        val scroll = rememberScrollState()
        // узнаём ориентацию экрана
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Стрик
            if (streakVal > 0) {
                Text(
                    text = pluralStringResource(R.plurals.streak_days, streakVal, streakVal),
                    fontSize = 20.sp
                )
            } else {
                Text(
                    text = stringResource(R.string.habit_start_message),
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            if (isLandscape) {
                // две строки по две кнопки
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onNavigate(Routes.PLAN) }, Modifier.weight(1f)) {
                        Text(stringResource(R.string.daily_plan))
                    }
                    Button(onClick = { onNavigate(Routes.REVIEW) }, Modifier.weight(1f)) {
                        Text(stringResource(R.string.review_day))
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onNavigate(Routes.HISTORY) }, Modifier.weight(1f)) {
                        Text(stringResource(R.string.history))
                    }
                    Button(onClick = { onNavigate(Routes.STATS) }, Modifier.weight(1f)) {
                        Text(stringResource(R.string.statistics))
                    }
                }
            } else {
                // портрет: четыре кнопки в столбик
                Button(onClick = { onNavigate(Routes.PLAN) }, Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.daily_plan))
                }
                Button(onClick = { onNavigate(Routes.REVIEW) }, Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.review_day))
                }
                Button(onClick = { onNavigate(Routes.HISTORY) }, Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.history))
                }
                Button(onClick = { onNavigate(Routes.STATS) }, Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.statistics))
                }
            }

            Spacer(Modifier.height(32.dp))

            // Помощник разработчика (не меняем)
            Box {
                Button(onClick = { devMenuExpanded = true }) {
                    Text(stringResource(R.string.help_dev))
                }
                DropdownMenu(
                    expanded = devMenuExpanded,
                    onDismissRequest = { devMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.dev_prev_day)) },
                        onClick = {
                            devMenuExpanded = false
                            onDebugPrevDay()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.dev_next_day)) },
                        onClick = {
                            devMenuExpanded = false
                            onDebugNextDay()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.dev_run_rollover)) },
                        onClick = {
                            devMenuExpanded = false
                            onRunRolloverNow()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.dev_reset)) },
                        onClick = {
                            devMenuExpanded = false
                            onDebugReset()
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.current_date, currentDate),
                fontSize = 14.sp
            )
        }
    }
}