package com.devden.quizly.ui.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devden.quizly.data.local.CategoryScore
import com.devden.quizly.data.local.ScoreDataStore
import com.devden.quizly.data.model.QuizCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val scoreDataStore: ScoreDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val score = savedStateHandle.get<Int>("score") ?: 0
    private val correctAnswers = savedStateHandle.get<Int>("correctAnswers") ?: 0
    private val totalQuestions = savedStateHandle.get<Int>("totalQuestions") ?: 10
    private val maxStreak = savedStateHandle.get<Int>("maxStreak") ?: 0
    private val categoryName = savedStateHandle.get<String>("category")
    private val category = categoryName?.let { QuizCategory.valueOf(it) } ?: QuizCategory.SCIENCE
    private val isNewBest = savedStateHandle.get<Boolean>("isNewBest") ?: false
    private val previousBest = savedStateHandle.get<Int>("previousBest") ?: 0

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            scoreDataStore.getAllCategoryStats().collect { categories ->
                val bestCategory = categories.maxByOrNull { it.accuracy }

                _uiState.value = ResultsUiState(
                    score = score,
                    correctAnswers = correctAnswers,
                    totalQuestions = totalQuestions,
                    maxStreak = maxStreak,
                    category = category,
                    isNewPersonalBest = isNewBest,
                    previousBest = previousBest,
                    allCategoryScores = categories,
                    bestCategory = bestCategory?.category
                )
            }
        }
    }
}

data class ResultsUiState(
    val score: Int = 0,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val maxStreak: Int = 0,
    val category: QuizCategory? = null,
    val isNewPersonalBest: Boolean = false,
    val previousBest: Int = 0,
    val allCategoryScores: List<CategoryScore> = emptyList(),
    val bestCategory: QuizCategory? = null
) {
    val percentage: Float
        get() = if (totalQuestions > 0) {
            (correctAnswers.toFloat() / totalQuestions) * 100
        } else 0f

    val incorrectAnswers: Int
        get() = totalQuestions - correctAnswers

    val achievementLevel: AchievementLevel
        get() = when {
            percentage >= 90 -> AchievementLevel.OUTSTANDING
            percentage >= 75 -> AchievementLevel.EXCELLENT
            percentage >= 60 -> AchievementLevel.GREAT
            percentage >= 50 -> AchievementLevel.GOOD
            else -> AchievementLevel.KEEP_PRACTICING
        }
}

enum class AchievementLevel(val title: String, val emoji: String) {
    OUTSTANDING("Outstanding!", "🌟"),
    EXCELLENT("Excellent!", "🎉"),
    GREAT("Great Job!", "👏"),
    GOOD("Good Effort!", "💪"),
    KEEP_PRACTICING("Keep Practicing!", "📚")
}

