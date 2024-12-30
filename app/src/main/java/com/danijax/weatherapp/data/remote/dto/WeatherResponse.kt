package com.danijax.weatherapp.data.remote.dto

import com.danijax.weatherapp.domain.model.WeatherInfo

data class WeatherResponse(
    val current: Current,
    val location: Location
)