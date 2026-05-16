package com.raitha.bharosahub.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.raitha.bharosahub.ui.onboarding.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class ProfileDataStore(private val context: Context) {
    companion object {
        val NAME = stringPreferencesKey("user_name")
        val CROP = stringPreferencesKey("primary_crop")
        val LANG = stringPreferencesKey("app_lang")
        val LOCATION = stringPreferencesKey("location")
        val PLOT_SIZE = stringPreferencesKey("plot_size")
    }

    val userProfile: Flow<UserProfile?> = context.dataStore.data.map { prefs ->
        val name = prefs[NAME]
        if (name != null) {
            UserProfile(
                name = name,
                primaryCrop = prefs[CROP] ?: "sugarcane",
                lang = prefs[LANG] ?: "en",
                location = prefs[LOCATION] ?: "",
                plotSize = prefs[PLOT_SIZE] ?: "1"
            )
        } else null
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[NAME] = profile.name
            prefs[CROP] = profile.primaryCrop
            prefs[LANG] = profile.lang
            prefs[LOCATION] = profile.location
            prefs[PLOT_SIZE] = profile.plotSize
        }
    }

    suspend fun clearProfile() {
        context.dataStore.edit { it.clear() }
    }
}
