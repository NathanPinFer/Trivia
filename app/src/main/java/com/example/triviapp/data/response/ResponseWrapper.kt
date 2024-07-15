package com.example.triviapp.data.response

import com.google.gson.annotations.SerializedName

data class ResponseWrapper(
    @SerializedName("response_code") val response_code: Int,
    @SerializedName("results") val results: List<QuestionResponse>
)
