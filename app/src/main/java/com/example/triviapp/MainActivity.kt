package com.example.triviapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triviapp.presentation.OptionsScreen
import com.example.triviapp.presentation.TriviaScreen
import com.example.triviapp.presentation.model.Routes
import com.example.triviapp.ui.theme.TriviAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TriviAppTheme {
                val navigationController = rememberNavController()
                NavHost(
                    navController = navigationController,
                    startDestination = Routes.OptionsScreen.route
                ) {
                    composable(Routes.OptionsScreen.route) { OptionsScreen(navController = navigationController) }
                    composable(Routes.TriviaScreen.route) {
                        TriviaScreen()
                    }
                }

            }
        }
    }
}

