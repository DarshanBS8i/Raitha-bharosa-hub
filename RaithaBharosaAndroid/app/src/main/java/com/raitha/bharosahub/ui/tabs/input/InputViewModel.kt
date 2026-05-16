package com.raitha.bharosahub.ui.tabs.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.bharosahub.data.local.DataGenerator
import com.raitha.bharosahub.data.repository.FarmRepository
import com.raitha.bharosahub.ui.onboarding.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InputViewModel(private val repository: FarmRepository) : ViewModel() {
    val profile = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun simulateData() {
        viewModelScope.launch {
            val profile = repository.userProfile.first()
            val crop = profile?.primaryCrop ?: "sugarcane"
            val location = profile?.location ?: "Field Alpha"
            
            val data = DataGenerator().generateRandomSoilData(crop = crop, location = location)
            repository.insertSoilData(data)
        }
    }

    fun saveManualData(moisture: Int, n: Int, p: Int, k: Int, crop: String, location: String, plotSize: String) {
        viewModelScope.launch {
            // Update profile first
            repository.saveProfile(
                UserProfile(
                    name = profile.value?.name ?: "User",
                    primaryCrop = crop,
                    lang = profile.value?.lang ?: "en",
                    location = location,
                    plotSize = plotSize
                )
            )

            // Then save soil data
            val data = com.raitha.bharosahub.data.local.SoilDataEntity(
                id = System.currentTimeMillis().toString(),
                date = java.time.LocalDateTime.now().toString(),
                crop = crop,
                moisture = moisture,
                nitrogen = n,
                phosphorus = p,
                potassium = k,
                location = location,
                plotSize = plotSize
            )
            repository.insertSoilData(data)
        }
    }
}
