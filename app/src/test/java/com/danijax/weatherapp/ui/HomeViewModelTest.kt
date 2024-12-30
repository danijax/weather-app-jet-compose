package com.danijax.weatherapp.ui

import com.danijax.weatherapp.MainCoroutineRule
import com.danijax.weatherapp.domain.model.UIResult
import com.danijax.weatherapp.domain.model.UISearchResult
import com.danijax.weatherapp.domain.model.WeatherInfo
import com.danijax.weatherapp.domain.repository.WeatherRepository
import com.danijax.weatherapp.domain.usecase.SearchWeatherUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class HomeViewModelTest{

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: HomeViewModel

    @Mock
    private lateinit var repository: WeatherRepository

    @Mock
    private lateinit var searchWeatherUseCase: SearchWeatherUseCase

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private val mockWeatherInfo = WeatherInfo(
        cityName = TODO(),
        temperature = TODO(),
        condition = TODO(),
        humidity = TODO(),
        uv = TODO(),
        iconUrl = TODO()
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = HomeViewModel(repository, searchWeatherUseCase)
    }

    @Test
    fun `when there is no saved city, emit Empty state`() = testScope.runBlockingTest {
        // Given
        `when`(repository.getSavedCity()).thenReturn(flowOf(null))

        // When
        val states = mutableListOf<WeatherUiState>()
        viewModel.uiState.collect { states.add(it) }

        // Then
        assertEquals(WeatherUiState.Empty, states.last())
    }

    @Test
    fun `when there is a saved city, fetch weather successfully`() = testScope.runBlockingTest {
        // Given
        val cityName = "London"
        `when`(repository.getSavedCity()).thenReturn(flowOf(cityName))
        `when`(repository.getWeatherForCity(cityName)).thenReturn(
            flowOf(UIResult.Success(mockWeatherInfo))
        )

        // When
        val states = mutableListOf<WeatherUiState>()
        viewModel.uiState.collect { states.add(it) }

        // Then
        assertEquals(WeatherUiState.Success(mockWeatherInfo), states.last())
    }

    @Test
    fun `when weather fetch fails, emit Error state`() = testScope.runBlockingTest {
        // Given
        val cityName = "London"
        val errorMessage = "Network error"
        `when`(repository.getSavedCity()).thenReturn(flowOf(cityName))
        `when`(repository.getWeatherForCity(cityName)).thenReturn(
            flowOf(UIResult.Error(errorMessage))
        )

        // When
        val states = mutableListOf<WeatherUiState>()
        viewModel.uiState.collect { states.add(it) }

        // Then
        assertEquals(WeatherUiState.Error(errorMessage), states.last())
    }

    @Test
    fun `when search query changes, debounce and emit results`() = testScope.runBlockingTest {
        // Given
        val query = "London"
        val searchResult = UISearchResult.Success(mockWeatherInfo)
        `when`(searchWeatherUseCase.invoke(query)).thenReturn(flowOf(searchResult))

        // When
        viewModel.onSearchQueryChanged(query)
        testScheduler.apply { advanceTimeBy(500); runCurrent() } // Account for debounce

        // Then
        assertEquals(
            WeatherSearchUiState.Success(mockWeatherInfo),
            viewModel.searchUiState.value
        )
    }

    @Test
    fun `when empty search query, emit Empty state`() = testScope.runBlockingTest {
        // Given
        val query = ""

        // When
        viewModel.onSearchQueryChanged(query)
        testScheduler.apply { advanceTimeBy(500); runCurrent() } // Account for debounce

        // Then
        assertEquals(WeatherSearchUiState.Empty, viewModel.searchUiState.value)
    }

    @Test
    fun `when updating saved city, repository is called`() = testScope.runBlockingTest {
        // Given
        val cityName = "London"

        // When
        viewModel.updateSavedCity(cityName)

        // Then
        verify(repository).saveCity(cityName)
    }
}