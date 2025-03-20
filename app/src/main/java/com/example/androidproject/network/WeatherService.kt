package com.example.androidproject.network

import kotlinx.serialization.Serializable

class WeatherService(val httpClient: HttpClient) {

    companion object {
        const val API_KEY = "7f59b667030145d68ae131104252003"
    }

    @Serializable
    data class Current(
        val last_updated_epoch: Int,
        val last_updated: String,
        val temp_c: Double,
        val temp_f: Double,
    )

    @Serializable
    data class WeatherResponse(val current: Current)

    suspend fun getWeatherData(city: String): Resource<WeatherResponse> {
        val url = "http://api.weatherapi.com/v1/current.json?key=${API_KEY}&q=${city}&aqi=no"
        return httpClient.get<WeatherResponse>(url)
    }

}