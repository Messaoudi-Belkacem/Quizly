package com.devden.quizly.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devden.quizly.data.model.AnswerOption
import com.devden.quizly.data.model.Difficulty
import com.devden.quizly.data.model.Question
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val categoryId: Int,
    val text: String,
    val optionsJson: String, // Serialized list of AnswerOption
    val correctAnswerId: String,
    val difficulty: String,
    val explanation: String?,
    val tagsJson: String // Serialized list of String
)

// Extension functions for conversion
fun QuestionEntity.toDomain(moshi: Moshi): Question {
    val optionsAdapter = moshi.adapter<List<AnswerOption>>(
        Types.newParameterizedType(List::class.java, AnswerOption::class.java)
    )
    val tagsAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    return Question(
        id = id,
        category = categoryIdToCategory(categoryId),
        questionText = text,
        options = optionsAdapter.fromJson(optionsJson)?.map { it.text } ?: emptyList(),
        correctAnswerIndex = findCorrectAnswerIndex(optionsJson, correctAnswerId, moshi),
        difficulty = Difficulty.valueOf(difficulty),
        timeLimit = when (Difficulty.valueOf(difficulty)) {
            Difficulty.EASY -> 30
            Difficulty.MEDIUM -> 45
            Difficulty.HARD -> 60
        }
    )
}

fun Question.toEntity(moshi: Moshi, categoryId: Int, correctAnswerId: String, explanation: String? = null, tags: List<String> = emptyList()): QuestionEntity {
    val optionsAdapter = moshi.adapter<List<AnswerOption>>(
        Types.newParameterizedType(List::class.java, AnswerOption::class.java)
    )
    val tagsAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    val answerOptions = options.mapIndexed { index, text ->
        AnswerOption(id = ('A' + index).toString(), text = text)
    }

    return QuestionEntity(
        id = id,
        categoryId = categoryId,
        text = questionText,
        optionsJson = optionsAdapter.toJson(answerOptions),
        correctAnswerId = correctAnswerId,
        difficulty = difficulty.name,
        explanation = explanation,
        tagsJson = tagsAdapter.toJson(tags)
    )
}

private fun findCorrectAnswerIndex(optionsJson: String, correctAnswerId: String, moshi: Moshi): Int {
    val adapter = moshi.adapter<List<AnswerOption>>(
        Types.newParameterizedType(List::class.java, AnswerOption::class.java)
    )
    val options = adapter.fromJson(optionsJson) ?: return 0
    return options.indexOfFirst { it.id == correctAnswerId }.coerceAtLeast(0)
}

private fun categoryIdToCategory(categoryId: Int): com.devden.quizly.data.model.QuizCategory {
    return when (categoryId) {
        1 -> com.devden.quizly.data.model.QuizCategory.SCIENCE
        2 -> com.devden.quizly.data.model.QuizCategory.HISTORY
        3 -> com.devden.quizly.data.model.QuizCategory.GEOGRAPHY
        4 -> com.devden.quizly.data.model.QuizCategory.LITERATURE
        5 -> com.devden.quizly.data.model.QuizCategory.VIDEO_GAMES
        6 -> com.devden.quizly.data.model.QuizCategory.TECHNOLOGY
        7 -> com.devden.quizly.data.model.QuizCategory.SPORTS
        8 -> com.devden.quizly.data.model.QuizCategory.FOOD
        else -> com.devden.quizly.data.model.QuizCategory.SCIENCE
    }
}

