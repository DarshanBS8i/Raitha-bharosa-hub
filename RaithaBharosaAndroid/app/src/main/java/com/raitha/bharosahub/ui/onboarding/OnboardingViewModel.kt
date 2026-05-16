package com.raitha.bharosahub.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.bharosahub.data.repository.FarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val name: String = "",
    val location: String = "",
    val plotSize: String = "",
    val primaryCrop: String = "sugarcane"
)

class OnboardingViewModel(private val repository: FarmRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateName(name: String) { _uiState.update { it.copy(name = name) } }
    fun updateLocation(loc: String) { _uiState.update { it.copy(location = loc) } }
    fun updatePlotSize(size: String) { _uiState.update { it.copy(plotSize = size) } }
    fun updateCrop(crop: String) { _uiState.update { it.copy(primaryCrop = crop) } }

    fun saveProfile(lang: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.saveProfile(
                UserProfile(
                    name = _uiState.value.name,
                    primaryCrop = _uiState.value.primaryCrop,
                    lang = lang,
                    location = _uiState.value.location,
                    plotSize = _uiState.value.plotSize
                )
            )
            onComplete()
        }
    }
}
