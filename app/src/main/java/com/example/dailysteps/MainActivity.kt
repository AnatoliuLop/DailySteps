// MainActivity.kt
package com.example.dailysteps

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.os.LocaleListCompat
import androidx.appcompat.app.AppCompatDelegate
import com.example.dailysteps.data.ServiceLocator
import com.example.dailysteps.ui.navigation.DailyStepsNavGraph
import com.example.dailysteps.ui.theme.DailyStepsTheme
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val prefs       = ServiceLocator.preferences
            val isDark      by prefs.isDarkTheme.collectAsState(initial = false)
            val localeTag   by prefs.locale     .collectAsState(initial = "en")

            // 1) переключаем AppCompat-локаль (нужна для стандартных View и system UI)
            LaunchedEffect(localeTag) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(localeTag)
                )
            }

            // 2) создаём новый Context с нужной локалью для Compose
            val baseContext = LocalContext.current
            val localeContext = remember(localeTag) {
                baseContext.updateLocale(Locale.forLanguageTag(localeTag))
            }

            CompositionLocalProvider(
                LocalContext provides localeContext,
                // RTL пока не поддерживаем, оставляем LTR
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                DailyStepsTheme(darkTheme = isDark) {
                    DailyStepsNavGraph()
                }
            }
        }
    }
}
