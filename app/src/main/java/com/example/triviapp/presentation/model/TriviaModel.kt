package com.example.triviapp.presentation.model

data class TriviaModel (
    val type: String,
    val difficulty: String,
    val category: Int,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)