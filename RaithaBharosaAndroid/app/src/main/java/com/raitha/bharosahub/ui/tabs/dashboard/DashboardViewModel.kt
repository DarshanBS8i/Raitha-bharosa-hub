package com.raitha.bharosahub.ui.tabs.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.bharosahub.data.remote.WeatherApi
import com.raitha.bharosahub.data.repository.FarmRepository
import com.raitha.bharosahub.domain.SowingIndexCalculator
import com.raitha.bharosahub.ui.onboarding.UserProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val profile: UserProfile? = null,
    val latestMoisture: Int = 0,
    val recommendation: SowingRecommendation = SowingRecommendation(),
    val currentTemp: Int = 25,
    val rainProb: Int = 10,
    val yieldSuggestion: String = ""
)

data class SowingRecommendation(val index: Int = 0, val messageKey: String = "advisory_normal_conditions", val canSow: Boolean = true)

class DashboardViewModel(private val repository: FarmRepository, private val weatherApi: WeatherApi) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                _uiState.update { it.copy(profile = profile) }
                profile?.location?.let { if (it.isNotBlank()) fetchWeatherForLocation(it) }
            }
        }
        viewModelScope.launch {
            repository.latestSoilData.collect { data ->
                if (data != null) {
                    _uiState.update { it.copy(latestMoisture = data.moisture) }
                    updateDynamicInsights(data.moisture, data.nitrogen, data.phosphorus, data.potassium)
                }
            }
        }
    }

    private fun fetchWeatherForLocation(location: String) {
        viewModelScope.launch {
            try {
                val response = weatherApi.getForecastByCity(location)
                val first = response.list.first()
                val temp = first.main.temp_max.toInt()
                val rain = (first.pop * 100).toInt()
                _uiState.update { it.copy(currentTemp = temp, rainProb = rain) }
                
                val state = _uiState.value
                val index = SowingIndexCalculator.calculate(state.latestMoisture, temp, rain, 150, 40, 180, 10)
                val msg = when {
                    rain > 60 -> "advisory_heavy_rain"
                    state.latestMoisture > 35 -> "advisory_soil_too_wet"
                    state.latestMoisture < 20 -> "advisory_soil_too_dry"
                    index > 70 -> "advisory_optimal_moisture"
                    else -> "advisory_normal_conditions"
                }
                _uiState.update { it.copy(recommendation = SowingRecommendation(index, msg, index > 40)) }
            } catch (e: Exception) {}
        }
    }

    private fun updateDynamicInsights(m: Int, n: Int, p: Int, k: Int) {
        val suggestion = when {
            n < 100 || p < 30 || k < 120 -> "yield_npk_low"
            m > 38 -> "yield_high_moisture"
            m < 15 -> "yield_low_moisture"
            else -> "yield_weeding"
        }
        _uiState.update { it.copy(yieldSuggestion = suggestion) }
    }
}
