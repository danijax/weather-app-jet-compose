package com.danijax.weatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.danijax.weatherapp.core.util.Graph
import com.danijax.weatherapp.ui.MainHomeScreen
import com.danijax.weatherapp.ui.MainHomeSearchBar
import com.danijax.weatherapp.ui.WeatherSearchScreen

@Composable
fun HomeWeatherNavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.HOME,
    ) {
        //authNavGraph(navController = navController)
        composable(route = Graph.HOME) {
            MainHomeScreen()
        }

        composable(route = Graph.SEARCH) {
            WeatherSearchScreen()
        }
    }
}