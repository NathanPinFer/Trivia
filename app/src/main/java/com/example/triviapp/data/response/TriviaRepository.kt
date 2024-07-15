package com.example.triviapp.data.response

import com.example.triviapp.data.TriviaApiService
import com.example.triviapp.presentation.model.TriviaModel
import javax.inject.Inject

class TriviaRepository @Inject constructor(private val api: TriviaApiService) {

    suspend fun getTriviaQuestionsFromRepository(category: Int, difficulty: String): Result<List<TriviaModel>> {
        return try {
            val response = api.getQuestions(category, difficulty)
            when (response.response_code) {
                0 -> Result.success(response.results.map { questionResponse -> questionResponse.toPresentation() })
                1 -> Result.failure(Exception("No Results: The API doesn't have enough questions for your query."))
                2 -> Result.failure(Exception("Invalid Parameter: Contains an invalid parameter."))
                3 -> Result.failure(Exception("Token Not Found: Session Token does not exist."))
                4 -> Result.failure(Exception("Token Empty: Session Token has returned all possible questions for the specified query."))
                else -> Result.failure(Exception("Unknown Error: The API returned an unknown response code."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




}