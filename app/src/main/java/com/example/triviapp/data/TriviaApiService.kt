package com.example.triviapp.data

import com.example.triviapp.data.response.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TriviaApiService {

    @GET("/api.php")
    suspend fun getQuestions(
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("amount") amount: Int = 10,
        @Query("type") type: String = "multiple"
    ): ResponseWrapper
}