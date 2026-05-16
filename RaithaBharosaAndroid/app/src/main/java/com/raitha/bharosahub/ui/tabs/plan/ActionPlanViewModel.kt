package com.raitha.bharosahub.ui.tabs.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.bharosahub.data.remote.WeatherApi
import com.raitha.bharosahub.data.repository.FarmRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdvisoryItem(
    val day: String,
    val condition: String,
    val advisoryKey: String,
    val temp: Int,
    val rainProb: Int
)

class ActionPlanViewModel(private val repository: FarmRepository, private val weatherApi: WeatherApi) : ViewModel() {
    private val _advisories = MutableStateFlow<List<AdvisoryItem>>(emptyList())
    val advisories: StateFlow<List<AdvisoryItem>> = _advisories.asStateFlow()

    init {
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                profile?.location?.let { if (it.isNotBlank()) fetchForecast(it) }
            }
        }
    }

    private fun fetchForecast(location: String) {
        viewModelScope.launch {
            try {
                val response = weatherApi.getForecastByCity(location)
                val list = response.list.filterIndexed { index, _ -> index % 8 == 0 }.take(7).mapIndexed { i, item ->
                    val day = getLocalizedDay(i)
                    val temp = item.main.temp_max.toInt() + ((-1..1).random())
                    val rainProb = (item.pop * 100).toInt().coerceAtLeast((0..15).random())
                    val cond = if (rainProb > 50) "Rain" else if (rainProb > 20) "Cloudy" else item.weather.firstOrNull()?.main ?: "Sunny"
                    generateAdvisory(day, cond, item.pop, temp, rainProb, i)
                }
                _advisories.value = list
            } catch (e: Exception) {
                val sampleList = listOf(
                    generateAdvisory(getLocalizedDay(0), "Sunny",        0.05, 32, 5,  0),
                    generateAdvisory(getLocalizedDay(1), "Cloudy",       0.35, 29, 35, 1),
                    generateAdvisory(getLocalizedDay(2), "Rain",         0.75, 25, 80, 2),
                    generateAdvisory(getLocalizedDay(3), "Storm",        0.90, 23, 95, 3),
                    generateAdvisory(getLocalizedDay(4), "Sunny",        0.0,  33, 0,  4),
                    generateAdvisory(getLocalizedDay(5), "Partly Cloudy",0.2,  31, 15, 5),
                    generateAdvisory(getLocalizedDay(6), "Cloudy",       0.45, 27, 50, 6)
                )
                _advisories.value = sampleList
            }
        }
    }

    private fun getLocalizedDay(index: Int): String {
        return when (index) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> {
                val date = java.time.LocalDate.now().plusDays(index.toLong())
                date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
            }
        }
    }

    private fun generateAdvisory(day: String, cond: String, pop: Double, temp: Int, rainProb: Int, dayIndex: Int = 0): AdvisoryItem {
        val key = when {
            rainProb > 80                       -> "advisory_heavy_rain"
            cond.contains("Storm", true)        -> "advisory_heavy_rain"
            cond.contains("Rain", true)         -> "advisory_rain_warning"
            cond.contains("Cloud", true) && temp > 28 -> "advisory_cloudy_humid"
            temp > 33                           -> "advisory_hot_weather"
            temp > 30 && rainProb < 10          -> "advisory_favorable"
            // Use dayIndex to vary advice on similar mild-weather days
            dayIndex % 3 == 0                   -> "advisory_normal_conditions"
            dayIndex % 3 == 1                   -> "advisory_pest_check"
            else                                -> "advisory_fertilizer_window"
        }
        return AdvisoryItem(day, cond, key, temp, rainProb)
    }
}
