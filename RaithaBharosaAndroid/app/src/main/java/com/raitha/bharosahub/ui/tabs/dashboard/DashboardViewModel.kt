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
    val latestN: Int = 150,
    val latestP: Int = 40,
    val latestK: Int = 180,
    val latestPlotSize: Int = 10,
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
                updateDynamicInsights()
            }
        }
        viewModelScope.launch {
            repository.latestSoilData.collect { data ->
                if (data != null) {
                    val plotSizeInt = data.plotSize.toIntOrNull() ?: 10
                    _uiState.update { it.copy(
                        latestMoisture = data.moisture,
                        latestN = data.nitrogen,
                        latestP = data.phosphorus,
                        latestK = data.potassium,
                        latestPlotSize = plotSizeInt
                    ) }
                    updateDynamicInsights()
                    recalculateSowingIndex()
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
                recalculateSowingIndex()
            } catch (e: Exception) {}
        }
    }

    private fun recalculateSowingIndex() {
        val state = _uiState.value
        val index = SowingIndexCalculator.calculate(
            state.latestMoisture, 
            state.currentTemp, 
            state.rainProb, 
            state.latestN, 
            state.latestP, 
            state.latestK, 
            state.latestPlotSize
        )
        val msg = when {
            state.rainProb > 60 -> "advisory_heavy_rain"
            state.latestMoisture > 35 -> "advisory_soil_too_wet"
            state.latestMoisture < 20 -> "advisory_soil_too_dry"
            index > 70 -> "advisory_optimal_moisture"
            else -> "advisory_normal_conditions"
        }
        _uiState.update { it.copy(recommendation = SowingRecommendation(index, msg, index > 40)) }
    }

    private fun updateDynamicInsights() {
        val state = _uiState.value
        val m = state.latestMoisture
        val n = state.latestN
        val p = state.latestP
        val k = state.latestK
        
        val lang = state.profile?.lang ?: "en"
        val crop = state.profile?.primaryCrop ?: "sugarcane"
        
        val isKn = lang.lowercase() == "kn"
        
        val hasNoData = state.latestMoisture == 0 && state.latestN == 150 && state.latestP == 40 && state.latestK == 180 && state.latestPlotSize == 10
        if (hasNoData) {
            val defaultMsg = if (isKn) {
                "ಯಾವುದೇ ಮಣ್ಣಿನ ಡೇಟಾ ದಾಖಲಾಗಿಲ್ಲ. ಮಣ್ಣಿನ ತೇವಾಂಶ ಮತ್ತು NPK ಮೌಲ್ಯಗಳನ್ನು ನಮೂದಿಸಲು ದಯವಿಟ್ಟು 'Input Data' ಟ್ಯಾಬ್‌ಗೆ ಹೋಗಿ."
            } else {
                "No soil data recorded yet. Please go to the 'Input Data' tab to enter soil moisture and NPK values."
            }
            _uiState.update { it.copy(yieldSuggestion = defaultMsg) }
            return
        }

        // Define crop-specific thresholds
        val (idealMoistureMin, idealMoistureMax) = when (crop.lowercase()) {
            "sugarcane" -> Pair(30, 45)
            "paddy" -> Pair(40, 60)
            "ragi" -> Pair(20, 30)
            else -> Pair(25, 40)
        }
        
        val (idealNMin, idealNMax) = when (crop.lowercase()) {
            "sugarcane" -> Pair(120, 180)
            "paddy" -> Pair(90, 130)
            "ragi" -> Pair(50, 80)
            else -> Pair(80, 150)
        }
        
        val (idealPMin, _) = when (crop.lowercase()) {
            "sugarcane" -> Pair(35, 50)
            "paddy" -> Pair(25, 40)
            "ragi" -> Pair(20, 35)
            else -> Pair(25, 45)
        }
        
        val (idealKMin, _) = when (crop.lowercase()) {
            "sugarcane" -> Pair(150, 200)
            "paddy" -> Pair(80, 120)
            "ragi" -> Pair(40, 70)
            else -> Pair(60, 130)
        }
        
        val sb = StringBuilder()
        
        // 1. Moisture suggestion
        if (isKn) {
            sb.append(when {
                m < idealMoistureMin -> "• ಮಣ್ಣು ಒಣಗಿದೆ ($m%). ಬೆಳೆ ಒಣಗದಂತೆ ತಡೆಯಲು ನೀರಾವರಿ ಒದಗಿಸಿ."
                m > idealMoistureMax -> "• ಮಣ್ಣಿನಲ್ಲಿ ತೇವಾಂಶ ಹೆಚ್ಚಾಗಿದೆ ($m%). ಬೇರು ಕೊಳೆಯುವುದನ್ನು ತಪ್ಪಿಸಲು ನೀರು ಬಸಿದು ಹೋಗಲು ದಾರಿ ಮಾಡಿ."
                else -> "• ಮಣ್ಣಿನ ತೇವಾಂಶವು ಉತ್ತಮವಾಗಿದೆ ($m%). ಇದು ಬೆಳೆ ಹೀರಿಕೊಳ್ಳಲು ಸೂಕ್ತವಾಗಿದೆ."
            }).append("\n\n")
        } else {
            sb.append(when {
                m < idealMoistureMin -> "• Soil is dry ($m%). Apply light irrigation to prevent moisture stress."
                m > idealMoistureMax -> "• Soil is waterlogged ($m%). Ensure proper drainage to avoid root rot."
                else -> "• Soil moisture is optimal ($m%). Great for crop absorption."
            }).append("\n\n")
        }
        
        // 2. Nitrogen suggestion
        if (isKn) {
            sb.append(when {
                n < idealNMin -> "• ಸಾರಜನಕ ಕಡಿಮೆ ಇದೆ ($n ppm). ಹಸಿರು ಎಲೆಗಳ ಬೆಳವಣಿಗೆಗೆ ಯೂರಿಯಾವನ್ನು ಶಿಫಾರಸು ಮಾಡಿದ ಪ್ರಮಾಣದಲ್ಲಿ ಬಳಸಿ."
                n > idealNMax -> "• ಸಾರಜನಕ ಹೆಚ್ಚಾಗಿದೆ ($n ppm). ಕೀಟಗಳ ಬಾಧೆ ತಡೆಯಲು ಹೆಚ್ಚಿನ ಯೂರಿಯಾ ಬಳಕೆಯನ್ನು ತಪ್ಪಿಸಿ."
                else -> "• ಸಾರಜನಕ ಸಮರ್ಪಕವಾಗಿದೆ ($n ppm). ಉತ್ತಮ ಸಸ್ಯ ಬೆಳವಣಿಗೆ ಕಂಡುಬಂದಿದೆ."
            }).append("\n\n")
        } else {
            sb.append(when {
                n < idealNMin -> "• Nitrogen is low ($n ppm). Apply Urea or Neem-coated urea to boost leafy green growth."
                n > idealNMax -> "• Nitrogen is high ($n ppm). Avoid extra urea to prevent excessive leaf growth and pest attacks."
                else -> "• Nitrogen is optimal ($n ppm). Good vegetative vigor."
            }).append("\n\n")
        }
        
        // 3. Phosphorus suggestion
        if (isKn) {
            sb.append(when {
                p < idealPMin -> "• ರಂಜಕ ಕಡಿಮೆ ಇದೆ ($p ppm). ಬೇರುಗಳ ಬಲವರ್ಧನೆಗೆ ಡಿಎಪಿ (DAP) ಅಥವಾ ಎಸ್ಎಸ್ಪಿ (SSP) ಗೊಬ್ಬರ ಹಾಕಿ."
                else -> "• ರಂಜಕ ಸಮರ್ಪಕವಾಗಿದೆ ($p ppm). ಬಲಿಷ್ಠ ಬೇರುಗಳ ಬೆಳವಣಿಗೆಗೆ ಸಹಕಾರಿಯಾಗಿದೆ."
            }).append("\n\n")
        } else {
            sb.append(when {
                p < idealPMin -> "• Phosphorus is low ($p ppm). Add DAP or SSP to enhance root establishment and tillering."
                else -> "• Phosphorus is optimal ($p ppm). Strong root foundation."
            }).append("\n\n")
        }
        
        // 4. Potassium suggestion
        if (isKn) {
            sb.append(when {
                k < idealKMin -> "• ಪೊಟ್ಯಾಸಿಯಮ್ ಕಡಿಮೆ ಇದೆ ($k ppm). ರೋಗ ನಿರೋಧಕ ಶಕ್ತಿ ಮತ್ತು ಧಾನ್ಯದ ತೂಕ ಹೆಚ್ಚಿಸಲು ಎಂಒಪಿ (MOP) ಗೊಬ್ಬರ ಬಳಸಿ."
                else -> "• ಪೊಟ್ಯಾಸಿಯಮ್ ಸಮರ್ಪಕವಾಗಿದೆ ($k ppm). ಉತ್ತಮ ರೋಗ ನಿರೋಧಕ ಶಕ್ತಿ ಮತ್ತು ಧಾನ್ಯ ತುಂಬಲು ಸಹಕಾರಿಯಾಗಿದೆ."
            }).append("\n\n")
        } else {
            sb.append(when {
                k < idealKMin -> "• Potassium is low ($k ppm). Add MOP to improve crop immunity, sugar translocation, and grain weight."
                else -> "• Potassium is optimal ($k ppm). Excellent grain filling and disease resistance."
            }).append("\n\n")
        }
        
        // 5. Crop specific quality advice
        if (isKn) {
            sb.append(when (crop.lowercase()) {
                "sugarcane" -> "💡 ಕಬ್ಬಿನ ಸಲಹೆ: ಪೊಟ್ಯಾಸಿಯಮ್ ಅನ್ನು ಉತ್ತಮ ಮಟ್ಟದಲ್ಲಿ ನಿರ್ವಹಿಸುವುದರಿಂದ ಕಬ್ಬಿನ ರಸದ ಸಕ್ಕರೆ ಪ್ರಮಾಣ (ಬ್ರಿಕ್ಸ್%) ಮತ್ತು ಕಾಂಡದ ದಪ್ಪ ಹೆಚ್ಚಾಗುತ್ತದೆ."
                "paddy" -> "💡 ಭತ್ತದ ಸಲಹೆ: ಈ ಹಂತದಲ್ಲಿ ಸಾಕಷ್ಟು ರಂಜಕ ಒದಗಿಸುವುದರಿಂದ ಹೆಚ್ಚು ಕವಲುಗಳು ಒಡೆಯುತ್ತವೆ. ಗದ್ದೆಯಲ್ಲಿ 2-5 ಸೆಂ.ಮೀ ನೀರು ನಿಲ್ಲಿಸಿ."
                "ragi" -> "💡 ರಾಗಿಯ ಸಲಹೆ: ರಾಗಿ ಮಧ್ಯಮ ತೇವಾಂಶದಲ್ಲಿ ಚೆನ್ನಾಗಿ ಬೆಳೆಯುತ್ತದೆ. ಹೆಚ್ಚು ಸಾರಜನಕವು ಗಿಡಗಳು ವಾಲುವುದಕ್ಕೆ ಕಾರಣವಾಗಬಹುದು; ಸಮತೋಲನ ಕಾಯ್ದುಕೊಳ್ಳಿ."
                else -> "💡 ಕೃಷಿ ಸಲಹೆ: ಪೋಷಕಾಂಶಗಳನ್ನು ಬೆಳೆಗಳು ಪೂರ್ಣ ಪ್ರಮಾಣದಲ್ಲಿ ಹೀರಿಕೊಳ್ಳಲು ಕಳೆಗಳನ್ನು ನಿಯಂತ್ರಣದಲ್ಲಿಡಿ."
            })
        } else {
            sb.append(when (crop.lowercase()) {
                "sugarcane" -> "💡 Sugarcane Tip: Maintaining high Potassium (K) now directly increases the juice sugar content (Brix%) and stalk thickness."
                "paddy" -> "💡 Paddy Tip: Adequate Phosphorus (P) at this stage encourages early tillering. Keep standing water at 2-5 cm."
                "ragi" -> "💡 Ragi Tip: Ragi thrives in moderate moisture. High Nitrogen can cause weak stems (lodging); keep it balanced."
                else -> "💡 Farm Tip: Keep a clean plot and remove weeds early to ensure full nutrient absorption by your crops."
            })
        }
        
        _uiState.update { it.copy(yieldSuggestion = sb.toString()) }
    }
}
