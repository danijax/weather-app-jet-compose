package com.danijax.weatherapp.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.danijax.weatherapp.core.util.Result
import com.danijax.weatherapp.core.util.from
import com.danijax.weatherapp.data.local.LocalDataSource
import com.danijax.weatherapp.data.remote.DataSource
import com.danijax.weatherapp.data.remote.WeatherApi
import com.danijax.weatherapp.domain.model.UIResult
import com.danijax.weatherapp.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val dataSource: DataSource,
    private val dataStore: LocalDataSource
) : WeatherRepository {

    override suspend fun getWeatherForCity(cityName: String): Flow<UIResult<WeatherInfo>> {
        val response  = dataSource.getWeatherForCity(cityName).first()

        return when(response){
            is Result.Success -> {
                 flowOf(UIResult.Success(response.data.from()))
            }
            is Result.Error -> {
                flowOf(UIResult.Error(response.string.error.message))
            }
        }
    }

    override  fun getSavedCity()= flow {
        val city = dataStore.getSavedCity().first()
        emit( city)
    }

    override suspend fun saveCity(cityName: String) {
        dataStore.saveCity(cityName)
    }
}