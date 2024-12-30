package com.danijax.weatherapp.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.danijax.weatherapp.data.local.LocalDataSource
import com.danijax.weatherapp.data.remote.DataSource
import com.danijax.weatherapp.data.remote.RemoteDataSource
import com.danijax.weatherapp.data.remote.WeatherApi
import com.danijax.weatherapp.domain.repository.WeatherRepository
import com.danijax.weatherapp.domain.repository.WeatherRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        dataSource: DataSource,
        dataStore: LocalDataSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(dataSource, dataStore)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(weatherApi: WeatherApi): DataSource {
        return RemoteDataSource(weatherApi)

    }

    @Provides
    @Singleton
    fun provideLocalDataSource(dataStore: DataStore<Preferences>): LocalDataSource {
        return com.danijax.weatherapp.data.local.WeatherAppPreference(dataStore)

    }

}