package com.example.triviapp.presentation


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triviapp.data.response.TriviaState
import com.example.triviapp.presentation.model.Routes
import com.example.triviapp.presentation.model.TriviaModel


@Composable
fun TriviaScreen(triviaViewModel: TriviaViewModel, navController: NavController) {

    val triviaState by triviaViewModel.triviaState.collectAsState()

    when (val state = triviaState) {
        is TriviaState.Loading -> LoadingIndicator()
        is TriviaState.QuestionState -> TriviaQuiz(state, triviaViewModel)
        is TriviaState.Error -> ErrorMessage(state.message)
        is TriviaState.GameOver ->
            GameOverDialog(state.finalScore, onPlayAgain = {
                triviaViewModel.resetGame()
                navController.navigate(Routes.OptionsScreen.route) {
                    popUpTo(Routes.TriviaScreen.route) { inclusive = true }
                }
            })
    }
}


@Composable
fun TriviaQuiz(state: TriviaState.QuestionState, triviaViewModel: TriviaViewModel) {
    val selectedAnswer by triviaViewModel.selectedAnswer.collectAsState()
    val preSelectedAnswer by triviaViewModel.preSelectedAnswer.collectAsState()
    val lives by triviaViewModel.lives.collectAsState()

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LivesDisplay(lives = lives)

            Spacer(modifier = Modifier.size(60.dp))

            QuestionCard(question = state.question)

            Spacer(modifier = Modifier.size(30.dp))

            GridAnswers(
                allAnswers = state.question.incorrect_answers + state.question.correct_answer,
                onAnswerSelected = { answer ->
                    triviaViewModel.selectAnswer(answer)

                },
                selectedAnswer = state.selectedAnswer,
                correctAnswer = if (state.isAnswered) state.question.correct_answer else null,
                preSelectedAnswer = preSelectedAnswer,
                isAnswered = state.isAnswered
            )

            Spacer(modifier = Modifier.size(20.dp))

            if (!state.isAnswered) {
                ConfirmButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { triviaViewModel.confirmAnswer() },
                    isEnabled = selectedAnswer != null
                )
            } else {
                NextQuestionButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    isEnabled = true,
                    onClick = {
                        triviaViewModel.nextQuestion()
                    })
            }


        }
    }
}


@Composable
fun GridAnswers(
    allAnswers: List<String>,
    onAnswerSelected: (String) -> Unit,
    preSelectedAnswer: String?,
    selectedAnswer: String?,
    correctAnswer: String?,
    isAnswered: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)
    ) {
        items(allAnswers.size) { index ->
            val answer = allAnswers[index]
            AnswerCard(
                answer = answer,
                onAnswerSelected = {
                    onAnswerSelected(answer)
                }, isEnabled = !isAnswered,
                isPreselected = answer == preSelectedAnswer,
                isSelected = answer == selectedAnswer,
                isCorrect = answer == correctAnswer,
                showResult = isAnswered
            )
        }
    }
}

@Composable
fun QuestionCard(question: TriviaModel?) {
    if (question == null) {
        Text(text = "No questions avaliable")
    } else {
        Box(
            modifier = Modifier
        ) {
            ElevatedCard(
                modifier = Modifier
                    .height(200.dp)
                    .width(380.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = question.question,
                        Modifier.padding(24.dp),
                        fontSize = 24.sp
                    )
                }

            }
        }

    }

}

@Composable
fun AnswerCard(
    answer: String,
    modifier: Modifier = Modifier,
    onAnswerSelected: () -> Unit,
    isEnabled: Boolean,
    isPreselected: Boolean,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean
) {


    val backgroundColor = when {
        showResult && isCorrect -> Color.Green.copy(alpha = 0.3f)
        showResult && isSelected -> Color.Red.copy(alpha = 0.3f)
        isSelected -> Color(0xFF6650a4).copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isPreselected || isSelected -> Color(0xFF6650a4)
        else -> Color.Transparent
    }

    val borderWidth = if (isPreselected || isSelected || (showResult && isCorrect)) 8.dp else 0.dp

    ElevatedCard(
        modifier = modifier
            .height(180.dp)
            .width(200.dp)
            .border(borderWidth, borderColor, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor
        ),
        onClick = onAnswerSelected, enabled = isEnabled && !showResult,

        ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = answer,
                Modifier.padding(24.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
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

@Composable
fun NextQuestionButton(modifier: Modifier, onClick: () -> Unit, isEnabled: Boolean) {
    Column(modifier = modifier) {
        TextButton(onClick = onClick, enabled = isEnabled) {
            Text(text = "Next Question!", fontSize = 45.sp)
        }
    }

}

@Composable
fun ConfirmButton(modifier: Modifier, onClick: () -> Unit, isEnabled: Boolean) {
    Column(modifier = modifier) {
        TextButton(onClick = onClick, enabled = isEnabled) {
            Text(text = "Confirm", fontSize = 45.sp)
        }
    }
}

@Composable
fun GameOverDialog(score: Int, onPlayAgain: () -> Unit) {
    AlertDialog(onDismissRequest = { /*TODO*/ }, title = {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Score: $score points")
        }
    }, confirmButton = {
        TextButton(onClick = onPlayAgain) {
            Text(text = "Play Again!", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        }
    })
}

@Composable
fun LivesDisplay(lives: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Lives",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = lives.toString(),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


