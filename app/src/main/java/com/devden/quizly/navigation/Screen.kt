package com.devden.quizly.navigation

import com.devden.quizly.data.model.QuizCategory

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quiz : Screen("quiz/{category}") {
        fun createRoute(category: QuizCategory) = "quiz/${category.name}"
    }
    object Result : Screen("result/{score}/{correctAnswers}/{totalQuestions}/{maxStreak}/{category}/{isNewBest}/{previousBest}") {
        fun createRoute(
            score: Int,
            correctAnswers: Int,
            totalQuestions: Int,
            maxStreak: Int,
            category: QuizCategory,
            isNewBest: Boolean,
            previousBest: Int
        ) = "result/$score/$correctAnswers/$totalQuestions/$maxStreak/${category.name}/$isNewBest/$previousBest"
    }
    object Stats : Screen("stats")
    object Settings : Screen("settings")
}


