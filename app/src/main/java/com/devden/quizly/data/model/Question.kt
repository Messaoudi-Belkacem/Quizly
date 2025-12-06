package com.devden.quizly.data.model

data class Question(
    val id: String,
    val category: QuizCategory,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val timeLimit: Int = 30 // seconds
)

enum class Difficulty {
    EASY, MEDIUM, HARD
}

