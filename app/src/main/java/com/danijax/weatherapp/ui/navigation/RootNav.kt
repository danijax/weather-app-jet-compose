package com.danijax.weatherapp.ui.navigation

import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.danijax.weatherapp.core.util.Graph
import com.danijax.weatherapp.ui.MainHomeSearchBar
import com.danijax.weatherapp.ui.SearchResultScreen

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.HOME,
    ) {
        composable(route = Graph.HOME) {
            MainHomeSearchBar(navController = navController)
        }

        composable(route = Graph.SEARCH) {
            SearchResultScreen(navController = navController)
        }


    }
}