package com.devden.quizly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devden.quizly.data.model.QuizCategory
import com.devden.quizly.data.repository.CategoryStats
import com.devden.quizly.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCategoryStats()
    }

    private fun loadCategoryStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val stats = QuizCategory.entries.map { category ->
                quizRepository.getCategoryStats(category)
            }

            _uiState.value = _uiState.value.copy(
                categoryStats = stats,
                isLoading = false
            )
        }
    }

    fun onCategorySelected(category: QuizCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onQuickStartClicked() {
        // Will be used to start a random quiz
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val categoryStats: List<CategoryStats> = emptyList(),
    val selectedCategory: QuizCategory? = null,
    val userStreak: Int = 0,
    val totalScore: Int = 0
)

