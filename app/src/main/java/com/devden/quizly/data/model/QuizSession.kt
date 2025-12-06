package com.devden.quizly.data.model

data class QuizSession(
    val category: QuizCategory,
    val questions: List<Question>,
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val answeredQuestions: List<AnsweredQuestion> = emptyList()
)

data class AnsweredQuestion(
    val question: Question,
    val userAnswerIndex: Int,
    val isCorrect: Boolean,
    val timeSpent: Int // seconds
)

data class QuizResult(
    val category: QuizCategory,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,
    val timeSpent: Int,
    val answeredQuestions: List<AnsweredQuestion>
) {
    val percentage: Float
        get() = if (totalQuestions > 0) (correctAnswers.toFloat() / totalQuestions) * 100 else 0f
}

