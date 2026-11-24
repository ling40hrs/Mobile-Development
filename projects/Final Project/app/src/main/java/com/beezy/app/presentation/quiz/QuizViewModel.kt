package com.beezy.app.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beezy.app.domain.repository.CardRepository
import com.beezy.app.domain.repository.DeckRepository
import com.beezy.app.domain.usecase.GenerateQuizUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(
    private val deckId: String,
    private val userId: String,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val generateQuizUseCase: GenerateQuizUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            try {
                val deck = deckRepository.getDeckById(deckId, userId)
                val deckCards = cardRepository.getCardsForDeck(deckId, userId)
                val allCards = cardRepository.getAllCards(userId)

                val questions = generateQuizUseCase(deckCards, allCards)

                _uiState.update {
                    it.copy(
                        deck = deck,
                        questions = questions,
                        currentIndex = 0,
                        selectedOptionIndex = null,
                        isAnswerRevealed = false,
                        score = 0,
                        isQuizComplete = false,
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Could not load this quiz. Please try again."
                    )
                }
            }
        }
    }

    fun selectOption(optionIndex: Int) {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        if (state.isAnswerRevealed) return

        val isCorrect = optionIndex == question.correctIndex
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerRevealed = true,
                score = newScore
            )
        }

        // Auto advance after 800ms
        viewModelScope.launch {
            delay(850)
            val nextIndex = state.currentIndex + 1
            if (nextIndex < state.questions.size) {
                _uiState.update {
                    it.copy(
                        currentIndex = nextIndex,
                        selectedOptionIndex = null,
                        isAnswerRevealed = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isQuizComplete = true)
                }
            }
        }
    }

    fun restartQuiz() {
        loadQuiz()
    }
}
