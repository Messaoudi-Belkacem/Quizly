package com.devden.quizly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devden.quizly.data.model.QuizCategory
import com.devden.quizly.ui.home.HomeScreen

@Composable
fun QuizlyNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onCategorySelected = { category ->
                    navController.navigate(Screen.Quiz.createRoute(category))
                },
                onQuickStartClicked = {
                    // Navigate to random quiz
                    val randomCategory = QuizCategory.values().random()
                    navController.navigate(Screen.Quiz.createRoute(randomCategory))
                },
                onStatsClicked = {
                    navController.navigate(Screen.Stats.route)
                },
                onSettingsClicked = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category")
            val category = categoryName?.let { QuizCategory.valueOf(it) } ?: QuizCategory.SCIENCE

            // Observe quiz state to navigate to results
            val viewModel: com.devden.quizly.ui.quiz.QuizViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isQuizComplete) {
                if (uiState.isQuizComplete) {
                    val correctAnswers = uiState.answeredQuestions.count { it.isCorrect }
                    navController.navigate(
                        Screen.Result.createRoute(
                            score = uiState.score,
                            correctAnswers = correctAnswers,
                            totalQuestions = uiState.questions.size,
                            maxStreak = uiState.maxStreak,
                            category = category,
                            isNewBest = uiState.isNewPersonalBest,
                            previousBest = uiState.previousBestScore
                        )
                    ) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            }

            com.devden.quizly.ui.quiz.QuizScreen(
                onNavigateBack = { navController.popBackStack() },
                onQuizComplete = { _, _ -> }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("correctAnswers") { type = NavType.IntType },
                navArgument("totalQuestions") { type = NavType.IntType },
                navArgument("maxStreak") { type = NavType.IntType },
                navArgument("category") { type = NavType.StringType },
                navArgument("isNewBest") { type = NavType.BoolType },
                navArgument("previousBest") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category")
            val category = categoryName?.let { QuizCategory.valueOf(it) } ?: QuizCategory.SCIENCE

            com.devden.quizly.ui.results.ResultsScreen(
                onPlayAgain = {
                    navController.navigate(Screen.Quiz.createRoute(category)) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onGoHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Stats.route) {
            com.devden.quizly.ui.stats.StatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            com.devden.quizly.ui.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}


