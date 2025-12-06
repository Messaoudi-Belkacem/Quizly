package com.devden.quizly.data.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuestionJsonRoot(
    val version: Int,
    val lastUpdated: String,
    val categories: List<CategoryJson>
)

@JsonClass(generateAdapter = true)
data class CategoryJson(
    val id: Int,
    val name: String,
    val icon: String,
    val color: String,
    val questions: List<QuestionJson>
)

@JsonClass(generateAdapter = true)
data class QuestionJson(
    val id: String,
    val text: String,
    val options: List<OptionJson>,
    val correctAnswerId: String,
    val difficulty: String,
    val explanation: String?,
    val tags: List<String>
)

@JsonClass(generateAdapter = true)
data class OptionJson(
    val id: String,
    val text: String
)

