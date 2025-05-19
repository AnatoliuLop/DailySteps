package com.example.dailysteps.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
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
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Text(stringResource(R.string.language))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (currentLocale == "en"),
                        onClick = { onLocaleChange("en") }
                    )
                    Text(stringResource(R.string.english))
                    Spacer(Modifier.width(16.dp))
                    RadioButton(
                        selected = (currentLocale == "sk"),
                        onClick = { onLocaleChange("sk") }
                    )
                    Text(stringResource(R.string.slovak))
                }


                Text(stringResource(R.string.theme))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.light))
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = currentTheme,
                        onCheckedChange = onThemeChange
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.dark))
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
