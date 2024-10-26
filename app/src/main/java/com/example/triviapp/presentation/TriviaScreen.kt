package com.example.triviapp.presentation


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun rememberWindowInfo(): com.example.triviapp.presentation.WindowInfo {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    return WindowInfo(
        screenWidthInfo = when {
            screenWidth < 400.dp -> WindowType.Compact
            screenWidth < 600.dp -> WindowType.Medium
            else -> WindowType.Expanded
        },
        screenHeightInfo = when {
            screenHeight < 480.dp -> WindowType.Compact
            screenHeight < 900.dp -> WindowType.Medium
            else -> WindowType.Expanded
        },
        screenWidth = screenWidth,
        screenHeight = screenHeight
    )
}

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
    val windowInfo = rememberWindowInfo()
    val selectedAnswer by triviaViewModel.selectedAnswer.collectAsState()
    val preSelectedAnswer by triviaViewModel.preSelectedAnswer.collectAsState()
    val lives by triviaViewModel.lives.collectAsState()

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize()
                .padding(top = 48.dp),
            verticalArrangement = Arrangement.spacedBy(
                when (windowInfo.screenHeightInfo) {
                    WindowType.Compact -> 8.dp
                    WindowType.Medium -> 16.dp
                    WindowType.Expanded -> 24.dp
                }
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LivesDisplay(
                lives = lives,
                modifier = Modifier.padding(
                    top = when (windowInfo.screenHeightInfo) {
                        WindowType.Compact -> 8.dp
                        else -> 16.dp
                    }
                )
            )

            QuestionCard(
                question = state.question,
                windowInfo = windowInfo
            )

            GridAnswers(
                allAnswers = state.question.incorrect_answers + state.question.correct_answer,
                onAnswerSelected = { answer -> triviaViewModel.selectAnswer(answer) },
                selectedAnswer = state.selectedAnswer,
                correctAnswer = if (state.isAnswered) state.question.correct_answer else null,
                preSelectedAnswer = preSelectedAnswer,
                isAnswered = state.isAnswered,
                windowInfo = windowInfo
            )

            if (!state.isAnswered) {
                ConfirmButton(
                    modifier = Modifier.padding(
                        bottom = when (windowInfo.screenHeightInfo) {
                            WindowType.Compact -> 8.dp
                            else -> 16.dp
                        }
                    ),
                    onClick = { triviaViewModel.confirmAnswer() },
                    isEnabled = selectedAnswer != null,
                    windowInfo = windowInfo
                )
            } else {
                NextQuestionButton(
                    modifier = Modifier.padding(
                        bottom = when (windowInfo.screenHeightInfo) {
                            WindowType.Compact -> 8.dp
                            else -> 16.dp
                        }
                    ),
                    isEnabled = true,
                    onClick = { triviaViewModel.nextQuestion() },
                    windowInfo = windowInfo
                )
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
    isAnswered: Boolean,
    windowInfo: com.example.triviapp.presentation.WindowInfo
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(
            when (windowInfo.screenWidthInfo) {
                WindowType.Compact -> 8.dp
                WindowType.Medium -> 12.dp
                WindowType.Expanded -> 16.dp
            }
        ),
        verticalArrangement = Arrangement.spacedBy(
            when (windowInfo.screenHeightInfo) {
                WindowType.Compact -> 8.dp
                WindowType.Medium -> 12.dp
                WindowType.Expanded -> 16.dp
            }
        ),
        modifier = Modifier.padding(8.dp)
    ) {
        items(allAnswers.size) { index ->
            val answer = allAnswers[index]
            AnswerCard(
                answer = answer,
                onAnswerSelected = { onAnswerSelected(answer) },
                isEnabled = !isAnswered,
                isPreselected = answer == preSelectedAnswer,
                isSelected = answer == selectedAnswer,
                isCorrect = answer == correctAnswer,
                showResult = isAnswered,
                windowInfo = windowInfo
            )
        }
    }
}

@Composable
fun QuestionCard(
    question: TriviaModel?,
    windowInfo: com.example.triviapp.presentation.WindowInfo
) {
    if (question == null) {
        Text(text = "No questions available")
    } else {
        ElevatedCard(
            modifier = Modifier
                .height(
                    when (windowInfo.screenHeightInfo) {
                        WindowType.Compact -> 120.dp
                        WindowType.Medium -> 160.dp
                        WindowType.Expanded -> 200.dp
                    }
                )
                .fillMaxWidth(
                    when (windowInfo.screenWidthInfo) {
                        WindowType.Compact -> 0.95f
                        WindowType.Medium -> 0.9f
                        WindowType.Expanded -> 0.85f
                    }
                )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.question,
                    Modifier.padding(16.dp),
                    fontSize = when (windowInfo.screenWidthInfo) {
                        WindowType.Compact -> 18.sp
                        WindowType.Medium -> 20.sp
                        WindowType.Expanded -> 24.sp
                    },
                    textAlign = TextAlign.Center
                )
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
    showResult: Boolean,
    windowInfo: com.example.triviapp.presentation.WindowInfo
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
            .fillMaxWidth()
            .aspectRatio(
                when (windowInfo.screenHeightInfo) {
                    WindowType.Compact -> 1.5f
                    WindowType.Medium -> 1.3f
                    WindowType.Expanded -> 1.1f
                }
            )
            .border(borderWidth, borderColor, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor
        ),
        onClick = onAnswerSelected,
        enabled = isEnabled && !showResult,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = answer,
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = when (windowInfo.screenWidthInfo) {
                    WindowType.Compact -> 16.sp
                    WindowType.Medium -> 20.sp
                    WindowType.Expanded -> 24.sp
                },
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
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
fun NextQuestionButton(
    modifier: Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean,
    windowInfo: com.example.triviapp.presentation.WindowInfo
) {
    TextButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
    ) {
        Text(
            text = "Next Question!",
            fontSize = when (windowInfo.screenWidthInfo) {
                WindowType.Compact -> 24.sp
                WindowType.Medium -> 32.sp
                WindowType.Expanded -> 45.sp
            }
        )
    }
}
@Composable
fun ConfirmButton(
    modifier: Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean,
    windowInfo: com.example.triviapp.presentation.WindowInfo
) {
    TextButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
    ) {
        Text(
            text = "Confirm",
            fontSize = when (windowInfo.screenWidthInfo) {
                WindowType.Compact -> 24.sp
                WindowType.Medium -> 32.sp
                WindowType.Expanded -> 45.sp
            }
        )
    }
}

@Composable
fun GameOverDialog(score: Int, onPlayAgain: () -> Unit) {
    AlertDialog(onDismissRequest = {}, title = {
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
fun LivesDisplay(lives: Int, modifier: Modifier = Modifier) {
    val windowInfo = rememberWindowInfo()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Lives",
            tint = Color.Red,
            modifier = Modifier.size(
                when (windowInfo.screenWidthInfo) {
                    WindowType.Compact -> 32.dp
                    WindowType.Medium -> 40.dp
                    WindowType.Expanded -> 48.dp
                }
            )
        )
        Text(
            text = lives.toString(),
            fontSize = when (windowInfo.screenWidthInfo) {
                WindowType.Compact -> 24.sp
                WindowType.Medium -> 30.sp
                WindowType.Expanded -> 36.sp
            },
            fontWeight = FontWeight.Bold
        )
    }
}


