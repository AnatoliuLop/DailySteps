
package com.example.dailysteps.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (streakVal > 0) {
                Text(
                    text = "Стрик: $streakVal ${if (streakVal == 1) "день" else "дня"} подряд",
                    fontSize = 20.sp
                )
            } else {
                Text(
                    text = "Поднажми — сегодня твой первый шаг к привычке!",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            Button(onClick = { onNavigate(Routes.PLAN) }, Modifier.fillMaxWidth()) {
                Text("Daily Plan")
            }
            Button(onClick = { onNavigate(Routes.REVIEW) }, Modifier.fillMaxWidth()) {
                Text("Review Day")
            }
            Button(onClick = { onNavigate(Routes.HISTORY) }, Modifier.fillMaxWidth()) {
                Text("History")
            }
            Button(onClick = { onNavigate(Routes.STATS) }, Modifier.fillMaxWidth()) {
                Text("Statistics")
            }

            Spacer(Modifier.height(32.dp))

            // Pomôcka Pre Developera
            Box {
                Button(onClick = { devMenuExpanded = true }) {
                    Text("Pomôcka Pre Developera")
                }
                DropdownMenu(
                    expanded = devMenuExpanded,
                    onDismissRequest = { devMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("⏮️ Prev Day") },
                        onClick = {
                            devMenuExpanded = false
                            onDebugPrevDay()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("⏭️ Next Day") },
                        onClick = {
                            devMenuExpanded = false
                            onDebugNextDay()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🚀 Run Rollover Now") },   // ← новая опция
                        onClick = {
                            devMenuExpanded = false
                            onRunRolloverNow()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🔄 Reset") },
                        onClick = {
                            devMenuExpanded = false
                            onDebugReset()
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Current date: $currentDate", fontSize = 14.sp)
        }
    }
}
