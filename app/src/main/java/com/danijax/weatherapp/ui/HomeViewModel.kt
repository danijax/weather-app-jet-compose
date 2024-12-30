package com.danijax.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danijax.weatherapp.domain.model.UIResult
import com.danijax.weatherapp.domain.model.UISearchResult
import com.danijax.weatherapp.domain.model.WeatherInfo
import com.danijax.weatherapp.domain.repository.WeatherRepository
import com.danijax.weatherapp.domain.usecase.SearchWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val searchWeatherUseCase: SearchWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val uiState: StateFlow<WeatherUiState> = fetchWeather()
        .stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = WeatherUiState.Loading
    )

    private val _searchUiState = MutableStateFlow<WeatherSearchUiState>(WeatherSearchUiState.Empty)
    val searchUiState: StateFlow<WeatherSearchUiState> = _searchUiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    private fun fetchWeather() = flow {
        val savedCity = repository.getSavedCity().first()
        if (savedCity != null) {
            repository.getWeatherForCity(savedCity).map {
                when (it) {
                    is UIResult.Success -> {
                        emit(WeatherUiState.Success(it.data))
                    }

                    is UIResult.Error -> {
                        emit(WeatherUiState.Error(it.message))
                    }

                    else -> {
                        emit(WeatherUiState.Empty)
                    }
                }
            }.collect()
        }
        else{
            emit(WeatherUiState.Empty)
        }


    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        viewModelScope.launch {
            searchWeatherUseCase.invoke(query)
                .debounce(500L)
                .distinctUntilChanged()
                .filterNot { query.isBlank() }
                .map { res ->
                    when (res) {
                        is UISearchResult.Empty -> _searchUiState.value = WeatherSearchUiState.Empty
                        is UISearchResult.Error -> _searchUiState.value = WeatherSearchUiState.Empty
                        is UISearchResult.NoAction -> _searchUiState.value =
                            WeatherSearchUiState.Empty

                        is UISearchResult.Success -> _searchUiState.value =
                            WeatherSearchUiState.Success(res.data)
                    }

                }.collect()
        }
    }

    fun updateSavedCity(cityName: String) {
        viewModelScope.launch {
            repository.saveCity(cityName)
        }
    }
}

sealed interface WeatherUiState {
    data object Empty : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val weatherInfo: WeatherInfo) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

sealed interface WeatherSearchUiState {
    data object Empty : WeatherSearchUiState
    data object Loading : WeatherSearchUiState
    data class Success(val weatherInfo: WeatherInfo) : WeatherSearchUiState
    data class Error(val message: String) : WeatherSearchUiState
}