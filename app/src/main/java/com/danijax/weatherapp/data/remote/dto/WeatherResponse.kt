package com.danijax.weatherapp.data.remote.dto

data class WeatherResponse(
    val current: Current,
    val location: Location
)