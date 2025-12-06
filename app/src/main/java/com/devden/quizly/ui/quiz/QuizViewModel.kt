package com.devden.quizly.ui.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devden.quizly.data.model.Question
import com.devden.quizly.data.model.QuizCategory
import com.devden.quizly.data.model.AnsweredQuestion
import com.devden.quizly.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val scoreDataStore: com.devden.quizly.data.local.ScoreDataStore,
    private val soundManager: com.devden.quizly.util.SoundManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryName = savedStateHandle.get<String>("category")
    private val category = categoryName?.let { QuizCategory.valueOf(it) } ?: QuizCategory.SCIENCE

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var categoryBestScore = 0

    init {
        loadCategoryBest()
        loadQuestions()
    }

    private fun loadCategoryBest() {
        viewModelScope.launch {
            scoreDataStore.getCategoryBest(category).collect { best ->
                categoryBestScore = best
            }
        }
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val questions = quizRepository.getQuestionsByCategory(category, limit = 10)

            if (questions.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No questions available for this category"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                questions = questions,
                currentQuestionIndex = 0,
                category = category,
                timeRemaining = questions.firstOrNull()?.timeLimit ?: 30
            )

            startTimer()
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0 &&
                   !_uiState.value.isAnswered &&
                   !_uiState.value.isQuizComplete) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    timeRemaining = _uiState.value.timeRemaining - 1
                )
            }

            // Time's up - mark as incorrect
            if (_uiState.value.timeRemaining == 0 && !_uiState.value.isAnswered) {
                onAnswerSelected(-1) // -1 indicates timeout
            }
        }
    }

    fun onAnswerSelected(answerIndex: Int) {
        if (_uiState.value.isAnswered) return

        val currentQuestion = _uiState.value.questions.getOrNull(_uiState.value.currentQuestionIndex) ?: return
        val isCorrect = answerIndex == currentQuestion.correctAnswerIndex

        // Play sound and vibrate based on answer
        if (isCorrect) {
            soundManager.playCorrectSound()
        } else {
            soundManager.playIncorrectSound()
        }

        val timeSpent = currentQuestion.timeLimit - _uiState.value.timeRemaining

        val answeredQuestion = AnsweredQuestion(
            question = currentQuestion,
            userAnswerIndex = answerIndex,
            isCorrect = isCorrect,
            timeSpent = timeSpent
        )

        val newScore = if (isCorrect) _uiState.value.score + 10 else _uiState.value.score
        val newStreak = if (isCorrect) _uiState.value.currentStreak + 1 else 0
        val maxStreak = maxOf(_uiState.value.maxStreak, newStreak)

        _uiState.value = _uiState.value.copy(
            isAnswered = true,
            selectedAnswerIndex = answerIndex,
            isCorrect = isCorrect,
            score = newScore,
            currentStreak = newStreak,
            maxStreak = maxStreak,
            answeredQuestions = _uiState.value.answeredQuestions + answeredQuestion
        )

        // Auto-advance to next question after delay
        viewModelScope.launch {
            delay(2500) // Show answer feedback for 2.5 seconds
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        val nextIndex = _uiState.value.currentQuestionIndex + 1

        if (nextIndex >= _uiState.value.questions.size) {
            // Quiz complete - play success sound
            soundManager.playSuccessSound()

            // Save results
            viewModelScope.launch {
                val correctAnswers = _uiState.value.answeredQuestions.count { it.isCorrect }
                val totalQuestions = _uiState.value.questions.size
                val finalScore = _uiState.value.score
                val isNewBest = finalScore > categoryBestScore

                scoreDataStore.saveQuizResult(
                    category = category,
                    score = finalScore,
                    correctAnswers = correctAnswers,
                    totalQuestions = totalQuestions,
                    isNewBest = isNewBest
                )

                _uiState.value = _uiState.value.copy(
                    isQuizComplete = true,
                    isNewPersonalBest = isNewBest,
                    previousBestScore = categoryBestScore
                )
            }
        } else {
            val nextQuestion = _uiState.value.questions[nextIndex]
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = nextIndex,
                isAnswered = false,
                selectedAnswerIndex = null,
                isCorrect = null,
                timeRemaining = nextQuestion.timeLimit
            )
            startTimer()
        }
    }

    fun toggleSound() {
        _uiState.value = _uiState.value.copy(
            soundEnabled = !_uiState.value.soundEnabled
        )
    }

    fun restartQuiz() {
        _uiState.value = QuizUiState()
        loadQuestions()
    }
}

data class QuizUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val category: QuizCategory? = null,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val timeRemaining: Int = 30,
    val isAnswered: Boolean = false,
    val selectedAnswerIndex: Int? = null,
    val isCorrect: Boolean? = null,
    val score: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val answeredQuestions: List<AnsweredQuestion> = emptyList(),
    val isQuizComplete: Boolean = false,
    val soundEnabled: Boolean = true,
    val isNewPersonalBest: Boolean = false,
    val previousBestScore: Int = 0
)

