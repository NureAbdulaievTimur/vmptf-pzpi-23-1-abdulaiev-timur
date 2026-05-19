package com.example.pz3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AllTasksScreen()
                }
            }
        }
    }
}

@Composable
fun AllTasksScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Level1Greeting()
        Spacer(modifier = Modifier.height(24.dp))

        Level2Calculator()
        Spacer(modifier = Modifier.height(24.dp))

        Level3Movies()
        Spacer(modifier = Modifier.height(24.dp))

        Level4Fitness()
    }
}

@Composable
fun Level1Greeting() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Рівень 1: Привітання", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Привіт, світ!",
                color = Color.Blue,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun Level2Calculator() {
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0.0") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Рівень 2: Калькулятор", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            OutlinedTextField(
                value = num1, onValueChange = { num1 = it },
                label = { Text("Перше число") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = num2, onValueChange = { num2 = it },
                label = { Text("Друге число") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { result = calculate(num1, num2, "+") }) { Text("+") }
                Button(onClick = { result = calculate(num1, num2, "-") }) { Text("-") }
                Button(onClick = { result = calculate(num1, num2, "*") }) { Text("*") }
                Button(onClick = { result = calculate(num1, num2, "/") }) { Text("/") }
            }

            Text("Результат: $result", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun calculate(n1: String, n2: String, operation: String): String {
    val a = n1.toDoubleOrNull() ?: return "Помилка введення"
    val b = n2.toDoubleOrNull() ?: return "Помилка введення"
    return when (operation) {
        "+" -> (a + b).toString()
        "-" -> (a - b).toString()
        "*" -> (a * b).toString()
        "/" -> if (b == 0.0) "Ділення на нуль!" else (a / b).toString()
        else -> "Помилка"
    }
}

data class Movie(val name: String, val genre: String, val rating: Double)

@Composable
fun Level3Movies() {
    val movies = listOf(
        Movie("Ефект метелика", "Психологічний трилер", 7.6),
        Movie("Джокер", "Драма", 8.3),
        Movie("Старим тут не місце", "Психологічний трилер", 8.2)
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Рівень 3: Фільми", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))

            movies.forEach { movie ->
                Text(movie.name, fontWeight = FontWeight.Bold)
                Text("Жанр: ${movie.genre} | Рейтинг: ${movie.rating}")
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
            }
        }
    }
}

data class Workout(val type: String, val duration: Int, val calories: Int)

@Composable
fun Level4Fitness() {
    var type by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    val workouts = remember { mutableStateListOf<Workout>() }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Рівень 4: Фітнес-трекер", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            OutlinedTextField(
                value = type, onValueChange = { type = it },
                label = { Text("Вид тренування") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = duration, onValueChange = { duration = it },
                label = { Text("Тривалість (хв)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = calories, onValueChange = { calories = it },
                label = { Text("Калорії") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (type.isNotBlank()) {
                        workouts.add(Workout(type, duration.toIntOrNull() ?: 0, calories.toIntOrNull() ?: 0))

                        type = ""
                        duration = ""
                        calories = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            ) {
                Text("Записати тренування")
            }

            if (workouts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Статистика:", fontWeight = FontWeight.Bold)
                val totalTime = workouts.sumOf { it.duration }
                val totalCal = workouts.sumOf { it.calories }
                Text("Загальний час: $totalTime хв | Спалено: $totalCal ккал")

                Spacer(modifier = Modifier.height(8.dp))
                workouts.forEach { w ->
                    Text("${w.type} (${w.duration} хв) - ${w.calories} ккал")
                }
            }
        }
    }
}