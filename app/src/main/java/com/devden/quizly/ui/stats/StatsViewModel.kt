package com.devden.quizly.ui.stats

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
class StatsViewModel @Inject constructor(
    private val scoreDataStore: ScoreDataStore,
    private val notificationManager: com.devden.quizly.util.NotificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load all statistics
            scoreDataStore.getAllCategoryStats().collect { categories ->
                scoreDataStore.totalScore.collect { totalScore ->
                    scoreDataStore.totalQuizzes.collect { totalQuizzes ->
                        scoreDataStore.currentStreak.collect { currentStreak ->
                            scoreDataStore.bestStreak.collect { bestStreak ->
                                _uiState.value = StatsUiState(
                                    isLoading = false,
                                    totalScore = totalScore,
                                    totalQuizzes = totalQuizzes,
                                    currentStreak = currentStreak,
                                    bestStreak = bestStreak,
                                    categoryScores = categories.filter { it.attempts > 0 },
                                    allCategoryScores = categories,
                                    badges = generateBadges(totalScore, totalQuizzes, currentStreak, bestStreak, categories)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun generateBadges(
        totalScore: Int,
        totalQuizzes: Int,
        currentStreak: Int,
        bestStreak: Int,
        categories: List<CategoryScore>
    ): List<Badge> {
        val badges = mutableListOf<Badge>()

        // Quiz completion badges
        badges.add(Badge(
            id = "first_quiz",
            title = "First Steps",
            description = "Complete your first quiz",
            icon = "🎯",
            isUnlocked = totalQuizzes >= 1
        ))

        badges.add(Badge(
            id = "quiz_10",
            title = "Dedicated Learner",
            description = "Complete 10 quizzes",
            icon = "📚",
            isUnlocked = totalQuizzes >= 10
        ))

        badges.add(Badge(
            id = "quiz_50",
            title = "Quiz Master",
            description = "Complete 50 quizzes",
            icon = "🎓",
            isUnlocked = totalQuizzes >= 50
        ))

        badges.add(Badge(
            id = "quiz_100",
            title = "Century Club",
            description = "Complete 100 quizzes",
            icon = "💯",
            isUnlocked = totalQuizzes >= 100
        ))

        // Score badges
        badges.add(Badge(
            id = "score_100",
            title = "Century Scorer",
            description = "Score 100+ points",
            icon = "⭐",
            isUnlocked = totalScore >= 100
        ))

        badges.add(Badge(
            id = "score_500",
            title = "Rising Star",
            description = "Score 500+ points",
            icon = "🌟",
            isUnlocked = totalScore >= 500
        ))

        badges.add(Badge(
            id = "score_1000",
            title = "Point Millionaire",
            description = "Score 1000+ points",
            icon = "💎",
            isUnlocked = totalScore >= 1000
        ))

        // Streak badges
        badges.add(Badge(
            id = "streak_3",
            title = "On Fire",
            description = "Maintain a 3-day streak",
            icon = "🔥",
            isUnlocked = bestStreak >= 3
        ))

        badges.add(Badge(
            id = "streak_7",
            title = "Week Warrior",
            description = "Maintain a 7-day streak",
            icon = "⚡",
            isUnlocked = bestStreak >= 7
        ))

        badges.add(Badge(
            id = "streak_30",
            title = "Unstoppable",
            description = "Maintain a 30-day streak",
            icon = "👑",
            isUnlocked = bestStreak >= 30
        ))

        // Category mastery badges
        val masterCategories = categories.filter { it.accuracy >= 90f && it.attempts >= 5 }
        badges.add(Badge(
            id = "category_master",
            title = "Category Master",
            description = "90%+ accuracy in any category",
            icon = "🏆",
            isUnlocked = masterCategories.isNotEmpty()
        ))

        badges.add(Badge(
            id = "all_categories",
            title = "Well Rounded",
            description = "Complete quizzes in all categories",
            icon = "🌈",
            isUnlocked = categories.all { it.attempts > 0 }
        ))

        return badges
    }

    fun refreshStats() {
        loadStats()
    }
}

data class StatsUiState(
    val isLoading: Boolean = false,
    val totalScore: Int = 0,
    val totalQuizzes: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val categoryScores: List<CategoryScore> = emptyList(),
    val allCategoryScores: List<CategoryScore> = emptyList(),
    val badges: List<Badge> = emptyList()
) {
    val averageScore: Float
        get() = if (totalQuizzes > 0) totalScore.toFloat() / totalQuizzes else 0f

    val unlockedBadges: List<Badge>
        get() = badges.filter { it.isUnlocked }

    val lockedBadges: List<Badge>
        get() = badges.filter { !it.isUnlocked }
}

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
)

