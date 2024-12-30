package com.danijax.weatherapp.data.remote

import com.danijax.weatherapp.data.remote.dto.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String
    ): Response<WeatherResponse>
}