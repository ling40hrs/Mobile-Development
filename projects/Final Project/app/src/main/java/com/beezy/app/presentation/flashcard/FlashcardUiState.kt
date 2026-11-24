package com.beezy.app.presentation.flashcard

import com.beezy.app.domain.model.Card
import com.beezy.app.domain.model.Deck

data class FlashcardUiState(
    val deck: Deck? = null,
    val cards: List<Card> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val cardsReviewedThisSession: Int = 0,
    val isSessionComplete: Boolean = false,
    val isShuffled: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val currentCard: Card? get() = cards.getOrNull(currentIndex)
    val totalCards: Int get() = cards.size
    val hasNext: Boolean get() = currentIndex < totalCards - 1
    val hasPrev: Boolean get() = currentIndex > 0
}
