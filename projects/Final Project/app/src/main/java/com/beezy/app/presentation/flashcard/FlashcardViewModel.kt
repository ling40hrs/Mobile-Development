package com.beezy.app.presentation.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beezy.app.domain.model.Card
import com.beezy.app.domain.repository.CardRepository
import com.beezy.app.domain.repository.DeckRepository
import com.beezy.app.domain.usecase.CalculateMaturityUseCase
import com.beezy.app.domain.usecase.CalculateSM2UseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardViewModel(
    private val deckId: String,
    private val userId: String,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val calculateSM2UseCase: CalculateSM2UseCase,
    private val calculateMaturityUseCase: CalculateMaturityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val deck = deckRepository.getDeckById(deckId, userId)
            val cards = cardRepository.getCardsForDeck(deckId, userId)
            _uiState.update {
                it.copy(
                    deck = deck,
                    cards = cards,
                    currentIndex = 0,
                    isFlipped = false,
                    isLoading = false
                )
            }
        }
    }

    fun toggleFlip() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun rateCard(grade: Int) {
        val currentCard = _uiState.value.currentCard ?: return
        viewModelScope.launch {
            val sm2Result = calculateSM2UseCase(
                currentReps = currentCard.reps,
                currentInterval = currentCard.interval,
                currentEaseFactor = currentCard.easeFactor,
                grade = grade
            )

            try {
                cardRepository.updateCardSM2(currentCard.id, sm2Result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not save your rating. Please try again.") }
                return@launch
            }

            val updatedCards = _uiState.value.cards.map { card ->
                if (card.id == currentCard.id) {
                    card.copy(
                        reps = sm2Result.reps,
                        interval = sm2Result.interval,
                        easeFactor = sm2Result.easeFactor,
                        dueDate = sm2Result.dueDate
                    )
                } else card
            }

            val nextIndex = _uiState.value.currentIndex + 1
            val isComplete = nextIndex >= updatedCards.size

            _uiState.update {
                it.copy(
                    cards = updatedCards,
                    isFlipped = false,
                    cardsReviewedThisSession = it.cardsReviewedThisSession + 1,
                    currentIndex = if (isComplete) it.currentIndex else nextIndex,
                    isSessionComplete = isComplete
                )
            }
        }
    }

    fun nextCard() {
        if (_uiState.value.hasNext) {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    isFlipped = false
                )
            }
        }
    }

    fun previousCard() {
        if (_uiState.value.hasPrev) {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex - 1,
                    isFlipped = false
                )
            }
        }
    }

    fun shuffleDeck() {
        _uiState.update {
            it.copy(
                cards = it.cards.shuffled(),
                currentIndex = 0,
                isFlipped = false,
                isShuffled = true
            )
        }
    }

    fun resetDeckReviewProgress() {
        viewModelScope.launch {
            cardRepository.resetDeckProgress(deckId, userId)
            loadSession()
        }
    }

    fun restartSession() {
        _uiState.update {
            it.copy(
                currentIndex = 0,
                isFlipped = false,
                isSessionComplete = false
            )
        }
    }
}
