package com.example.dailysteps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dailysteps.ui.navigation.DailyStepsNavGraph
import com.example.dailysteps.ui.theme.DailyStepsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyStepsTheme {
                DailyStepsNavGraph()
            }
        }
    }
}
