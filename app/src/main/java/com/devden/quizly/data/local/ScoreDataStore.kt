package com.devden.quizly.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.devden.quizly.data.model.QuizCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quiz_scores")

@Singleton
class ScoreDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val TOTAL_SCORE_KEY = intPreferencesKey("total_score")
        private val TOTAL_QUIZZES_KEY = intPreferencesKey("total_quizzes")
        private val CURRENT_STREAK_KEY = intPreferencesKey("current_streak")
        private val BEST_STREAK_KEY = intPreferencesKey("best_streak")
        private val LAST_QUIZ_DATE_KEY = longPreferencesKey("last_quiz_date")

        // Category-specific keys
        private fun categoryScoreKey(category: QuizCategory) =
            intPreferencesKey("category_score_${category.name}")
        private fun categoryBestKey(category: QuizCategory) =
            intPreferencesKey("category_best_${category.name}")
        private fun categoryAttemptsKey(category: QuizCategory) =
            intPreferencesKey("category_attempts_${category.name}")
        private fun categoryCorrectKey(category: QuizCategory) =
            intPreferencesKey("category_correct_${category.name}")
        private fun categoryTotalKey(category: QuizCategory) =
            intPreferencesKey("category_total_${category.name}")
    }

    // Get total score across all quizzes
    val totalScore: Flow<Int> = dataStore.data.map { preferences ->
        preferences[TOTAL_SCORE_KEY] ?: 0
    }

    // Get total number of quizzes completed
    val totalQuizzes: Flow<Int> = dataStore.data.map { preferences ->
        preferences[TOTAL_QUIZZES_KEY] ?: 0
    }

    // Get current daily streak
    val currentStreak: Flow<Int> = dataStore.data.map { preferences ->
        preferences[CURRENT_STREAK_KEY] ?: 0
    }

    // Get best streak ever
    val bestStreak: Flow<Int> = dataStore.data.map { preferences ->
        preferences[BEST_STREAK_KEY] ?: 0
    }

    // Save quiz results
    suspend fun saveQuizResult(
        category: QuizCategory,
        score: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        isNewBest: Boolean
    ) {
        dataStore.edit { preferences ->
            // Update total score
            val currentTotal = preferences[TOTAL_SCORE_KEY] ?: 0
            preferences[TOTAL_SCORE_KEY] = currentTotal + score

            // Update total quizzes
            val currentQuizzes = preferences[TOTAL_QUIZZES_KEY] ?: 0
            preferences[TOTAL_QUIZZES_KEY] = currentQuizzes + 1

            // Update category-specific stats
            val categoryScore = preferences[categoryScoreKey(category)] ?: 0
            preferences[categoryScoreKey(category)] = categoryScore + score

            val categoryAttempts = preferences[categoryAttemptsKey(category)] ?: 0
            preferences[categoryAttemptsKey(category)] = categoryAttempts + 1

            val categoryCorrect = preferences[categoryCorrectKey(category)] ?: 0
            preferences[categoryCorrectKey(category)] = categoryCorrect + correctAnswers

            val categoryTotal = preferences[categoryTotalKey(category)] ?: 0
            preferences[categoryTotalKey(category)] = categoryTotal + totalQuestions

            // Update category best if this is a new record
            if (isNewBest) {
                preferences[categoryBestKey(category)] = score
            }

            // Update last quiz date
            preferences[LAST_QUIZ_DATE_KEY] = System.currentTimeMillis()
        }
    }

    // Get best score for a category
    suspend fun getCategoryBest(category: QuizCategory): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[categoryBestKey(category)] ?: 0
        }
    }

    // Get category statistics
    fun getCategoryStats(category: QuizCategory): Flow<CategoryScore> {
        return dataStore.data.map { preferences ->
            CategoryScore(
                category = category,
                totalScore = preferences[categoryScoreKey(category)] ?: 0,
                bestScore = preferences[categoryBestKey(category)] ?: 0,
                attempts = preferences[categoryAttemptsKey(category)] ?: 0,
                correctAnswers = preferences[categoryCorrectKey(category)] ?: 0,
                totalQuestions = preferences[categoryTotalKey(category)] ?: 0
            )
        }
    }

    // Get all category statistics
    fun getAllCategoryStats(): Flow<List<CategoryScore>> {
        return dataStore.data.map { preferences ->
            QuizCategory.values().map { category ->
                CategoryScore(
                    category = category,
                    totalScore = preferences[categoryScoreKey(category)] ?: 0,
                    bestScore = preferences[categoryBestKey(category)] ?: 0,
                    attempts = preferences[categoryAttemptsKey(category)] ?: 0,
                    correctAnswers = preferences[categoryCorrectKey(category)] ?: 0,
                    totalQuestions = preferences[categoryTotalKey(category)] ?: 0
                )
            }
        }
    }

    // Update streak
    suspend fun updateStreak(newStreak: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENT_STREAK_KEY] = newStreak

            val currentBest = preferences[BEST_STREAK_KEY] ?: 0
            if (newStreak > currentBest) {
                preferences[BEST_STREAK_KEY] = newStreak
            }
        }
    }

    // Reset all data (for testing or user request)
    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class CategoryScore(
    val category: QuizCategory,
    val totalScore: Int,
    val bestScore: Int,
    val attempts: Int,
    val correctAnswers: Int,
    val totalQuestions: Int
) {
    val accuracy: Float
        get() = if (totalQuestions > 0) {
            (correctAnswers.toFloat() / totalQuestions) * 100
        } else 0f

    val averageScore: Float
        get() = if (attempts > 0) {
            totalScore.toFloat() / attempts
        } else 0f
}

