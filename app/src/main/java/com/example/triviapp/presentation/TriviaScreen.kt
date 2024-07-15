package com.example.triviapp.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triviapp.data.response.TriviaState
import com.example.triviapp.presentation.model.TriviaModel
import kotlin.random.Random


@Composable
fun TriviaScreen(triviaViewModel: TriviaViewModel = hiltViewModel()) {

    val triviaState by triviaViewModel.triviaState.collectAsState()

    when (val state = triviaState) {
        is TriviaState.Loading -> LoadingIndicator()
        is TriviaState.Success -> TriviaQuiz(state.questions.firstOrNull())
        is TriviaState.Error -> ErrorMessage(state.message)
    }


}

@Composable
fun TriviaQuiz(response: TriviaModel?) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        QuestionCard(response = response)
    }

}

@Composable
fun QuestionCard(response: TriviaModel?) {
    if (response == null) {
        Text(text = "No questions avaliable")
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ElevatedCard(
                modifier = Modifier
                    .height(200.dp)
                    .width(300.dp)
            ) {
                Text(text = response.question)

            }
        }
    }

}

@Composable
fun LoadingIndicator() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }

}

@Composable
fun ErrorMessage(message: String) {
    Text(text = message, color = Color.Red)
}




