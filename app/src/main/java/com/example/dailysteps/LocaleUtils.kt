package com.example.dailysteps

import android.content.Context
import android.content.res.Configuration
import java.util.Locale


fun Context.updateLocale(locale: Locale): Context {
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}
