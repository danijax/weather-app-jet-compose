package com.danijax.weatherapp.domain.model

data class WeatherInfo(
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val uv: Double,
    val iconUrl: String
)