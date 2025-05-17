package com.example.dailysteps.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailysteps.R
import kotlinx.coroutines.flow.StateFlow
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.ui.navigation.Routes
import com.example.dailysteps.ui.viewmodel.StepUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    streak: StateFlow<Int>,
    stepState: StateFlow<StepUiState>,
    onUpdateSteps: (Int) -> Unit,
    onNavigate: (String) -> Unit
) {
    val prefs = ServiceLocator.preferences

    val isDark by prefs.isDarkTheme.collectAsState(initial = false)
    val locale by prefs.locale.collectAsState(initial = "en")

// для вызова suspend-функций
    val scope = rememberCoroutineScope()

    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsDialog(
            currentLocale = locale,
            onLocaleChange = { newLoc ->
                scope.launch { prefs.setLocale(newLoc) }
            },
            currentTheme = isDark,
            onThemeChange = { newTheme ->
                scope.launch { prefs.setDarkTheme(newTheme) }
            },
            onDismiss = { showSettings = false }
        )
    }

    val streakVal by streak.collectAsState()
    val stepVal by stepState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Streak: $streakVal", fontSize = 20.sp)
            LinearProgressIndicator(
                progress = (stepVal.actual.toFloat() / stepVal.goal).coerceIn(0f,1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .progressSemantics()
            )
            Text("${stepVal.actual} / ${stepVal.goal} steps")

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
        }
    }
}
