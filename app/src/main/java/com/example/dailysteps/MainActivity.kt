// app/src/main/java/com/example/dailysteps/MainActivity.kt
package com.example.dailysteps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.ui.navigation.DailyStepsNavGraph
import com.example.dailysteps.ui.theme.DailyStepsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val prefs = ServiceLocator.preferences
            val isDark by prefs.isDarkTheme.collectAsState(initial = false)
            // Если потом заведёте локаль через CompositionLocal, подключайте её здесь:
            DailyStepsTheme(darkTheme = isDark) {
                DailyStepsNavGraph()
            }
        }
    }
}
