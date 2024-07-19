package com.example.triviapp.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.triviapp.presentation.model.Routes
import kotlin.random.Random

@Composable
fun OptionsScreen(
    navController: NavHostController,
    triviaViewModel: TriviaViewModel
) {
    val categories = listOf(
        "Any",
        "General Knowledge",
        "Books",
        "Films",
        "Music",
        "Musical & Theatres",
        "Television",
        "Video Games",
        "Board Games",
        "Science & Nature",
        "Computers",
        "Mathematics",
        "Mythology",
        "Sports",
        "Geography",
        "History",
        "Politics",
        "Art",
        "Celebrities",
        "Animals",
        "Vehicles",
        " Comics",
        "Gadgets",
        "Anime & Manga",
        "Cartoon & Animations"
    )
    val difficulties = listOf("Any", "Easy", "Medium", "Hard")
    var selectedCategory by rememberSaveable { mutableStateOf("Any") }
    var selectedDifficulty by rememberSaveable { mutableStateOf("Any") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 350.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            TitleOption(modifier = Modifier.padding(start = 90.dp), title = "Category:")

            Spacer(modifier = Modifier.size(5.dp))

            OptionSelector(modifier = Modifier.align(Alignment.CenterHorizontally),
                option = categories, selectedOption = selectedCategory,
                onOptionSelected = { category ->
                    selectedCategory = category
                })

            Spacer(modifier = Modifier.size(20.dp))

            TitleOption(modifier = Modifier.padding(start = 90.dp), title = "Difficulty:")

            Spacer(modifier = Modifier.size(5.dp))

            OptionSelector(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                option = difficulties,
                selectedOption = selectedDifficulty,
                onOptionSelected = { difficulty ->
                    selectedDifficulty = difficulty
                })

            Spacer(modifier = Modifier.size(30.dp))

            PlayButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    val selectedCategoryId = getCategoryId(selectedCategory)
                    val selectedFinalDifficulty = getDifficulty(selectedDifficulty)

                    triviaViewModel.selectedCategoryId = selectedCategoryId
                    triviaViewModel.selectedDifficulty = selectedFinalDifficulty

                    triviaViewModel.fetchTrivia()
                    navController.navigate(Routes.TriviaScreen.route)
                }

            )

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionSelector(
    modifier: Modifier,
    option: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {

    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ), shape = RoundedCornerShape(15.dp), textStyle = TextStyle(fontSize = 20.sp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                ScrollableColumn(
                    modifier = Modifier.heightIn(max = 290.dp)
                ) {
                    option.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = text)
                                }
                            },
                            onClick = {
                                onOptionSelected(text)
                                isExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                        if (index < option.size - 1) {
                            HorizontalDivider(color = Color(0xFF224B92))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleOption(modifier: Modifier, title: String) {
    Column(modifier = modifier) {
        Text(text = title)
    }
}

@Composable
fun PlayButton(modifier: Modifier, onClick: () -> Unit) {
    Column(modifier = modifier) {
        TextButton(onClick = onClick) {
            Text(text = "Play now!", fontSize = 45.sp)
        }
    }

}


@Composable
fun ScrollableColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        content()
    }
}

fun getCategoryId(selectedCategory: String): Int {
    return when (selectedCategory) {
        "Any" -> 0
        "General Knowledge" -> 9
        "Books" -> 10
        "Films" -> 11
        "Music" -> 12
        "Musical & Theatres" -> 13
        "Television" -> 14
        "Video Games" -> 15
        "Board Games" -> 16
        "Science & Nature" -> 17
        "Computers" -> 18
        "Mathematics" -> 19
        "Mythology" -> 20
        "Sports" -> 21
        "Geography" -> 22
        "History" -> 23
        "Politics" -> 24
        "Art" -> 25
        "Celebrities" -> 26
        "Animal" -> 27
        "Vehicles" -> 28
        "Animals" -> 29
        "Gadgets" -> 30
        "Anime & Manga" -> 31
        else -> 32
    }
}

fun getDifficulty(difficulty: String): String {
    return when (difficulty) {
        "Any" -> {
            when (getRandomNumDifficulty()) {
                1 -> "easy"
                2 -> "medium"
                else -> "hard"
            }
        }

        "Easy" -> "easy"
        "Medium" -> "medium"
        else -> "hard"
    }
}

fun getRandomNumDifficulty(): Int = Random.nextInt(1, 4)
