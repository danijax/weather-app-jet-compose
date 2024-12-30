package com.danijax.weatherapp.domain.usecase

import com.danijax.weatherapp.domain.model.UIResult
import com.danijax.weatherapp.domain.model.UISearchResult
import com.danijax.weatherapp.domain.model.WeatherInfo
import com.danijax.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class SearchWeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {

    suspend operator fun invoke(query: String) : Flow<UISearchResult<WeatherInfo>> {

        if (query.length < 2) {
            return flowOf(UISearchResult.NoAction)
        }
        return weatherRepository.getWeatherForCity(query)
            .map {
                when(it){
                    is UIResult.Success -> {
                        UISearchResult.Success(it.data)
                    }
                    is UIResult.Error ->{
                        UISearchResult.Empty
                    }

                    else -> {
                        UISearchResult.NoAction
                    }

                }

            }


    }
}