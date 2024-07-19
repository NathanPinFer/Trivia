package com.example.triviapp.data.response

import com.example.triviapp.presentation.model.TriviaModel

sealed class TriviaState {
    data object Loading : TriviaState()
    data class QuestionState(
        val question: TriviaModel,
        val score: Int,
        val lives: Int,
        val difficulty: String,
        val isAnswered: Boolean,
        val selectedAnswer: String?,
        val isCorrect: Boolean?
    ) : TriviaState()

    data class Error(val message: String) : TriviaState()
    data class GameOver(val finalScore: Int): TriviaState()
}