package com.example.dailysteps.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dailysteps.R

@Composable
fun SettingsDialog(
    currentLocale: String,
    onLocaleChange: (String) -> Unit,
    currentTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Language selection
                Text(text = stringResource(R.string.language))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (currentLocale == "en"),
                        onClick = { onLocaleChange("en") }
                    )
                    Text(text = stringResource(R.string.english))
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = (currentLocale == "sk"),
                        onClick = { onLocaleChange("sk") }
                    )
                    Text(text = stringResource(R.string.slovak))
                }

                // Theme selection
                Text(text = stringResource(R.string.theme))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.light))
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = currentTheme,
                        onCheckedChange = onThemeChange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.dark))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
