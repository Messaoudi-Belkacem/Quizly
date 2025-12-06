package com.devden.quizly.data.json

import android.content.Context
import android.util.Log
import com.devden.quizly.data.local.database.QuestionEntity
import com.devden.quizly.data.model.AnswerOption
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionJsonParser @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    private val adapter = moshi.adapter(QuestionJsonRoot::class.java)
    private val optionsAdapter = moshi.adapter<List<AnswerOption>>(
        Types.newParameterizedType(List::class.java, AnswerOption::class.java)
    )
    private val tagsAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    @Throws(IOException::class)
    fun parseQuestionsFromAsset(fileName: String = "questions.json"): List<QuestionEntity> {
        try {
            val json = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }

            val root = adapter.fromJson(json)
                ?: throw IOException("Invalid JSON format")

            Log.d("QuestionJsonParser", "Parsed ${root.categories.size} categories")

            return root.categories.flatMap { category ->
                category.questions.map { q ->
                    convertToEntity(q, category.id)
                }
            }
        } catch (e: Exception) {
            Log.e("QuestionJsonParser", "Error parsing JSON", e)
            throw e
        }
    }

    fun parseQuestionsByCategory(categoryId: Int, fileName: String = "${getCategoryFileName(categoryId)}.json"): List<QuestionEntity> {
        return try {
            val json = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }

            val root = adapter.fromJson(json)
                ?: throw IOException("Invalid JSON format")

            val category = root.categories.find { it.id == categoryId }
                ?: throw IOException("Category $categoryId not found")

            category.questions.map { q ->
                convertToEntity(q, category.id)
            }
        } catch (e: IOException) {
            Log.w("QuestionJsonParser", "Category file not found, returning empty list", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("QuestionJsonParser", "Error parsing category JSON", e)
            emptyList()
        }
    }

    private fun convertToEntity(questionJson: QuestionJson, categoryId: Int): QuestionEntity {
        val options = questionJson.options.map { AnswerOption(it.id, it.text) }

        return QuestionEntity(
            id = questionJson.id,
            categoryId = categoryId,
            text = questionJson.text,
            optionsJson = optionsAdapter.toJson(options),
            correctAnswerId = questionJson.correctAnswerId,
            difficulty = questionJson.difficulty,
            explanation = questionJson.explanation,
            tagsJson = tagsAdapter.toJson(questionJson.tags)
        )
    }

    private fun getCategoryFileName(categoryId: Int): String {
        return when (categoryId) {
            1 -> "science"
            2 -> "history"
            3 -> "geography"
            4 -> "literature"
            5 -> "movies"
            6 -> "games"
            7 -> "music"
            8 -> "technology"
            9 -> "sports"
            10 -> "food"
            else -> "questions"
        }
    }
}

