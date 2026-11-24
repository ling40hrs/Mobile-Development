package com.beezy.app.presentation.quiz

import com.beezy.app.domain.model.Deck
import com.beezy.app.domain.model.QuizQuestion

data class QuizUiState(
    val deck: Deck? = null,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val score: Int = 0,
    val isQuizComplete: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentIndex)
    val totalQuestions: Int get() = questions.size
    val scorePercentage: Int
        get() = if (totalQuestions == 0) 0 else ((score.toDouble() / totalQuestions) * 100).toInt()
}
