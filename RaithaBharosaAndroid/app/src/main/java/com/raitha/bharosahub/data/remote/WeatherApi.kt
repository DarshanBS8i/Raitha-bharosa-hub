package com.raitha.bharosahub.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class OpenWeatherResponse(val list: List<ForecastItem>)
data class ForecastItem(val dt: Long, val main: MainData, val weather: List<WeatherDescription>, val pop: Double)
data class MainData(val temp_max: Double, val temp_min: Double)
data class WeatherDescription(val id: Int, val main: String, val description: String)

interface WeatherApi {
    @GET("data/2.5/forecast")
    suspend fun getForecastByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = "b478ce3fbd089c4a8bca25c14b943608"
    ): OpenWeatherResponse
}

object WeatherClient {
    private const val BASE_URL = "https://api.openweathermap.org/"
    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}
