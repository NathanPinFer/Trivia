package com.example.triviapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviapp.data.response.TriviaRepository
import com.example.triviapp.data.response.TriviaState
import com.example.triviapp.presentation.model.TriviaModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TriviaViewModel @Inject constructor(private val repository: TriviaRepository) : ViewModel() {

    private val _triviaState = MutableStateFlow<TriviaState>(TriviaState.Loading)
    val triviaState: StateFlow<TriviaState> = _triviaState.asStateFlow()

    private val _isQuestionAnswered = MutableStateFlow(false)

    private val _preSelectedAnswer = MutableStateFlow<String?>(null)
    val preSelectedAnswer: StateFlow<String?> = _preSelectedAnswer.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

    private val _lives = MutableStateFlow(3)
    var lives: StateFlow<Int> = _lives.asStateFlow()


    var selectedCategoryId: Int = 9
    var selectedDifficulty: String = "easy"

    fun fetchTrivia() {
        Log.d(
            "TriviaViewModel",
            "fetchTrivia called with category: $selectedCategoryId, difficulty: $selectedDifficulty"
        )
        getTriviaQuestions(selectedCategoryId, selectedDifficulty)
    }

    private var currentQuestionIndex = 0
    private var score = 0
    private var questions: List<TriviaModel> = emptyList()

    private fun getTriviaQuestions(idCategory: Int, difficulty: String) {
        viewModelScope.launch {
            try {
                _triviaState.value = TriviaState.Loading
                Log.d("TriviaViewModel", "Starting to fetch trivia questions")
                val result = repository.getTriviaQuestionsFromRepository(idCategory, difficulty)
                _triviaState.value = when {
                    result.isSuccess -> {
                        questions = result.getOrNull() ?: emptyList()
                        Log.d(
                            "TriviaViewModel",
                            "Trivia questions fetched successfully: $questions"
                        )
                        TriviaState.QuestionState(
                            question = questions[currentQuestionIndex],
                            score = score,
                            lives = _lives.value,
                            isAnswered = false,
                            selectedAnswer = null,
                            isCorrect = null,
                            difficulty = questions[currentQuestionIndex].difficulty
                        )
                    }

                    result.isFailure -> {
                        val errorMessage =
                            result.exceptionOrNull()?.message ?: "Unknown error occurred"
                        Log.e("TriviaViewModel", "Error fetching trivia questions: $errorMessage")
                        TriviaState.Error(errorMessage)
                    }

                    else -> {
                        Log.e("TriviaViewModel", "Unknown error occurred")
                        TriviaState.Error("Unknown error occurred")
                    }
                }
            } catch (e: Exception) {
                Log.e("TriviaViewModel", "Exception in getTriviaQuestions: ${e.message}")
                _triviaState.value = TriviaState.Error("An unexpected error occurred: ${e.message}")
            }
            Log.d("TriviaViewModel", "Final state: ${_triviaState.value}")
        }
    }

    private fun answerQuestion(answer: String) {
        val currentState = triviaState.value as? TriviaState.QuestionState ?: return
        val currentQuestion = currentState.question

        _isQuestionAnswered.value = true

        val isCorrect = answer == currentQuestion.correct_answer
        if (isCorrect) {
            val difficultyScore = when (currentQuestion.difficulty) {
                "easy" -> 5
                "medium" -> 10
                "hard" -> 20
                else -> 0
            }
            score += difficultyScore
        } else {
            _lives.value--
        }

        if (_lives.value <= 0) {
            _triviaState.value = TriviaState.GameOver(score)
        } else {
            _triviaState.value = TriviaState.QuestionState(
                question = currentQuestion,
                score = score,
                lives = _lives.value,
                isAnswered = true,
                selectedAnswer = answer,
                isCorrect = isCorrect,
                difficulty = questions[currentQuestionIndex].difficulty
            )
        }
    }

    fun nextQuestion() {
        _isQuestionAnswered.value = false
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            _triviaState.value = TriviaState.QuestionState(
                question = questions[currentQuestionIndex],
                score = score,
                lives = _lives.value,
                isAnswered = false,
                selectedAnswer = null,
                isCorrect = null,
                difficulty = questions[currentQuestionIndex].difficulty
            )
        } else if (_lives.value > 0) {
            currentQuestionIndex = 0
            _lives.value++
            fetchTrivia()
        } else {
            _triviaState.value = TriviaState.GameOver(score)
        }
    }

    fun selectAnswer(answer: String) {
        _preSelectedAnswer.value = answer
        _selectedAnswer.value = answer
    }

    fun confirmAnswer() {
        val currentAnswer = _selectedAnswer.value ?: return
        answerQuestion(currentAnswer)
        _preSelectedAnswer.value = null
    }

    fun resetGame() {
        score = 0
        _lives.value = 3
        currentQuestionIndex = 0
        questions = emptyList()
        fetchTrivia()

    }


}





