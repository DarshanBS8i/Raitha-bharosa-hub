package com.raitha.bharosahub.util

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
    fun setLocale(context: Context, language: String): Context {
        saveLanguage(context, language)
        return updateResources(context, language)
    }

    fun wrap(context: Context): Context {
        val language = getSavedLanguage(context)
        return updateResources(context, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences("lang_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("app_lang", language).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("lang_prefs", Context.MODE_PRIVATE)
        return prefs.getString("app_lang", "en") ?: "en"
    }
}
