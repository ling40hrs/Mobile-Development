package com.beezy.app.presentation.study

import com.beezy.app.domain.model.Card
import com.beezy.app.domain.model.Deck

data class DeckStudyUiState(
    val deck: Deck? = null,
    val cards: List<Card> = emptyList(),
    val filteredCards: List<Card> = emptyList(),
    val searchQuery: String = "",
    val selectedCardTag: String? = null,
    val isAddEditCardDialogOpen: Boolean = false,
    val editingCard: Card? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
