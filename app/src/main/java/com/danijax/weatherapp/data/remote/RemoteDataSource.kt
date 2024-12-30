package com.danijax.weatherapp.data.remote

import com.danijax.weatherapp.core.util.Result
import com.danijax.weatherapp.core.util.apiRequestFlow
import com.danijax.weatherapp.data.remote.dto.WeatherResponse
import com.danijax.weatherapp.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val weatherApi: WeatherApi) : DataSource {
    override fun getWeatherForCity(cityName: String): Flow<Result<WeatherResponse>> {
        return apiRequestFlow { weatherApi.getCurrentWeather(cityName) }
    }

}