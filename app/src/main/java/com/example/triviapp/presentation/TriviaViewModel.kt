package com.example.triviapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviapp.data.response.TriviaRepository
import com.example.triviapp.data.response.TriviaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TriviaViewModel @Inject constructor(private val repository: TriviaRepository) : ViewModel() {

    private val _triviaState = MutableStateFlow<TriviaState>(TriviaState.Loading)
    val triviaState: StateFlow<TriviaState> get() = _triviaState

    fun getTriviaQuestions(idCategory: Int, difficulty: String) {
        viewModelScope.launch {
            _triviaState.value = TriviaState.Loading
            val result = repository.getTriviaQuestionsFromRepository(idCategory, difficulty)
            _triviaState.value = when {
                result.isSuccess -> TriviaState.Success(result.getOrNull() ?: emptyList())
                result.isFailure -> TriviaState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error occurred"
                )

                else -> TriviaState.Error("Unknow error occurred")

            }
            Log.i("TriviaViewModel", "State: ${_triviaState.value}")
        }
    }


}