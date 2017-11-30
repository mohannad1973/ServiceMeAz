package com.gropse.serviceme.utils

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.ContextThemeWrapper

import java.util.Locale

object LocaleUtils {

    private var sLocale: Locale? = null

    fun setLocale(locale: Locale) {
        sLocale = locale
        if (sLocale != null) {
            Locale.setDefault(sLocale)
        }
    }

    fun updateConfig(wrapper: ContextThemeWrapper) {
        if (sLocale != null) {
            val configuration = Configuration()
            configuration.setLocale(sLocale)
            wrapper.applyOverrideConfiguration(configuration)
        }
    }

    fun updateConfig(app: Application, configuration: Configuration) {
        if (sLocale != null) {
            //Wrapping the configuration to avoid Activity endless loop
            //val config = Configuration(configuration)
            configuration.setLocale(sLocale)
            val res = app.baseContext.resources
            res.updateConfiguration(configuration, res.displayMetrics)
        }
    }
}
