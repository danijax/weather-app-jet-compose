package com.danijax.weatherapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.danijax.weatherapp.core.util.Keys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherAppPreference @Inject constructor(private val dataStore: DataStore<Preferences>) :
    LocalDataSource {
    override fun getSavedCity(): Flow<String?> {
        return dataStore.data.map { pref ->
            pref[Keys.SAVED_CITY]
        }
    }

    override suspend fun saveCity(cityName: String) {
        dataStore.edit { preferences ->
            preferences[Keys.SAVED_CITY] = cityName
        }
    }
}