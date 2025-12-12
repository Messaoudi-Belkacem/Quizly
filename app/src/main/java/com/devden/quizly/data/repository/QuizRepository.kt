package com.devden.quizly.data.repository

import android.util.Log
import com.devden.quizly.data.json.QuestionJsonParser
import com.devden.quizly.data.local.database.QuestionDao
import com.devden.quizly.data.local.database.toDomain
import com.devden.quizly.data.model.Question
import com.devden.quizly.data.model.QuizCategory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val questionDao: QuestionDao,
    private val jsonParser: QuestionJsonParser,
    private val moshi: Moshi
) {

    companion object {
        private const val TAG = "QuizRepository"
    }

    // Reload questions from JSON on every app startup
    suspend fun reloadQuestionsFromJson() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Reloading questions from JSON...")
                val questions = jsonParser.parseQuestionsFromAsset()
                questionDao.clearAndInsert(questions)
                Log.d(TAG, "Successfully reloaded ${questions.size} questions")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reload questions from JSON", e)
            }
        }
    }

    // Check if database needs seeding (legacy method, kept for backward compatibility)
    suspend fun seedDatabaseIfNeeded() {
        withContext(Dispatchers.IO) {
            try {
                val questionCount = questionDao.getTotalQuestionCount()
                if (questionCount == 0) {
                    Log.d(TAG, "Database empty, loading from JSON...")
                    reloadQuestionsFromJson()
                } else {
                    Log.d(TAG, "Database already contains $questionCount questions (use reloadQuestionsFromJson to refresh)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to seed database", e)
            }
        }
    }

    // Get questions by category from database
    suspend fun getQuestionsByCategory(category: QuizCategory, limit: Int = 10): List<Question> {
        return withContext(Dispatchers.IO) {
            try {
                // Ensure database is seeded
                seedDatabaseIfNeeded()

                val categoryId = categoryToCategoryId(category)
                val entities = questionDao.getQuestionsByCategory(categoryId).first()

                if (entities.isEmpty()) {
                    Log.w(TAG, "No questions found for category $category, using fallback")
                    return@withContext getFallbackQuestions(category, limit)
                }

                // Simulate loading delay for UX
                delay(500)

                entities.shuffled()
                    .take(limit)
                    .map { it.toDomain(moshi) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading questions", e)
                getFallbackQuestions(category, limit)
            }
        }
    }

    // Get random questions from any category
    suspend fun getRandomQuestions(limit: Int = 10): List<Question> {
        return withContext(Dispatchers.IO) {
            try {
                seedDatabaseIfNeeded()

                val allCategories = QuizCategory.entries
                val randomCategory = allCategories.random()

                getQuestionsByCategory(randomCategory, limit)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading random questions", e)
                emptyList()
            }
        }
    }

    // Get category statistics
    suspend fun getCategoryStats(category: QuizCategory): CategoryStats {
        return withContext(Dispatchers.IO) {
            try {
                // Ensure database is seeded first
                seedDatabaseIfNeeded()

                val categoryId = categoryToCategoryId(category)
                val questionCount = questionDao.getQuestionCountByCategory(categoryId)

                CategoryStats(
                    category = category,
                    totalQuestions = questionCount,
                    averageScore = 0f,
                    highScore = 0
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error getting category stats", e)
                CategoryStats(category, 0, 0f, 0)
            }
        }
    }

    private fun categoryToCategoryId(category: QuizCategory): Int {
        return when (category) {
            QuizCategory.SCIENCE -> 1
            QuizCategory.HISTORY -> 2
            QuizCategory.GEOGRAPHY -> 3
            QuizCategory.LITERATURE -> 4
            QuizCategory.VIDEO_GAMES -> 5
            QuizCategory.TECHNOLOGY -> 6
            QuizCategory.SPORTS -> 7
            QuizCategory.FOOD -> 8
            QuizCategory.ISLAM -> 9
            QuizCategory.AI_ETHICS -> 10
            QuizCategory.RESPONSIBILITY_OF_SOCIAL_NETWORKS -> 11
            QuizCategory.ETHICS_IN_IOT -> 12
            QuizCategory.DIGITAL_REVOLUTION -> 13
        }
    }

    // Fallback questions in case database is empty
    private fun getFallbackQuestions(category: QuizCategory, limit: Int): List<Question> {
        return when (category) {
            QuizCategory.SCIENCE -> listOf(
                Question(
                    id = "sci_fallback_1",
                    category = QuizCategory.SCIENCE,
                    questionText = "What is the chemical symbol for water?",
                    options = listOf("H2O", "CO2", "O2", "NaCl"),
                    correctAnswerIndex = 0,
                    difficulty = com.devden.quizly.data.model.Difficulty.EASY
                )
            )
            QuizCategory.HISTORY -> listOf(
                Question(
                    id = "his_fallback_1",
                    category = QuizCategory.HISTORY,
                    questionText = "In which year did World War II end?",
                    options = listOf("1943", "1944", "1945", "1946"),
                    correctAnswerIndex = 2,
                    difficulty = com.devden.quizly.data.model.Difficulty.EASY
                )
            )
            else -> listOf(
                Question(
                    id = "general_fallback_1",
                    category = category,
                    questionText = "This is a sample question for ${category.displayName}",
                    options = listOf("Option A", "Option B", "Option C", "Option D"),
                    correctAnswerIndex = 0,
                    difficulty = com.devden.quizly.data.model.Difficulty.EASY
                )
            )
        }.take(limit)
    }
}

data class CategoryStats(
    val category: QuizCategory,
    val totalQuestions: Int,
    val averageScore: Float,
    val highScore: Int
)

