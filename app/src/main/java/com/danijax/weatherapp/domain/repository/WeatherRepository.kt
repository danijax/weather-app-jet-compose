package com.danijax.weatherapp.domain.repository

import com.danijax.weatherapp.core.util.Result
import com.danijax.weatherapp.domain.model.UIResult
import com.danijax.weatherapp.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherForCity(cityName: String): Flow<UIResult<WeatherInfo>>
    fun getSavedCity(): Flow<String?>
    suspend fun saveCity(cityName: String)
}