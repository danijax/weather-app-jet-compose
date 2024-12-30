package com.danijax.weatherapp.data.remote

import com.danijax.weatherapp.core.util.Result
import com.danijax.weatherapp.data.remote.dto.WeatherResponse
import com.danijax.weatherapp.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun getWeatherForCity(cityName: String): Flow<Result<WeatherResponse>>
}