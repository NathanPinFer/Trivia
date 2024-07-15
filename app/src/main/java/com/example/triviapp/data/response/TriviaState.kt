package com.example.triviapp.data.response

import com.example.triviapp.presentation.model.TriviaModel

sealed class TriviaState {
    data object Loading : TriviaState()
    data class Success(val questions: List<TriviaModel>) : TriviaState()
    data class Error(val message: String) : TriviaState()
}