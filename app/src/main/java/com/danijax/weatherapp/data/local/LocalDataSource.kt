package com.danijax.weatherapp.data.local

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getSavedCity(): Flow<String?>
    suspend fun saveCity(cityName: String)

}