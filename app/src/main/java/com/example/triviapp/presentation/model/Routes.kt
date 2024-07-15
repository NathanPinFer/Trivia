package com.example.triviapp.presentation.model

sealed class Routes(val route:String) {
    data object OptionsScreen:Routes("OptionsScreen")
    data object TriviaScreen:Routes("TriviaScreen")
}