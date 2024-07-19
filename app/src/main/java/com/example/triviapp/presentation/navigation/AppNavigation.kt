package com.example.triviapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triviapp.presentation.OptionsScreen
import com.example.triviapp.presentation.TriviaScreen
import com.example.triviapp.presentation.TriviaViewModel
import com.example.triviapp.presentation.model.Routes

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val viewModel: TriviaViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = Routes.OptionsScreen.route) {
        composable(Routes.OptionsScreen.route) {
            OptionsScreen(navController = navController, triviaViewModel = viewModel)
        }
        composable(Routes.TriviaScreen.route) {
            TriviaScreen(triviaViewModel = viewModel, navController = navController)
        }
    }
}