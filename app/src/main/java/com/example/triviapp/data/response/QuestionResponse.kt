package com.example.triviapp.data.response

import com.example.triviapp.presentation.model.TriviaModel
import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    @SerializedName("type") val type: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("category") val category: Int,
    @SerializedName("question") val question: String,
    @SerializedName("correct_answer") val correct_answer: String,
    @SerializedName("incorrect_answers") val incorrect_answers: List<String>
) {

    fun toPresentation(): TriviaModel {
        return TriviaModel(
            type = type,
            difficulty = difficulty,
            category = category,
            question = question,
            correct_answer = correct_answer,
            incorrect_answers = incorrect_answers
        )
    }
}