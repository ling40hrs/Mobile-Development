package com.beezy.app.presentation.decks

import com.beezy.app.domain.model.Deck

enum class DeckSortOption(val displayName: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    TITLE("Title (A-Z)"),
    CARDS_COUNT("Most Cards")
}

data class DecksUiState(
    val decks: List<Deck> = emptyList(),
    val filteredDecks: List<Deck> = emptyList(),
    val searchQuery: String = "",
    val allTags: List<String> = emptyList(),
    val selectedTag: String? = null,
    val sortOption: DeckSortOption = DeckSortOption.NEWEST,
    val isCreateDialogOpen: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
