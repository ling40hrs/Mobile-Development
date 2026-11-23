package com.beezy.app.presentation.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beezy.app.domain.model.Deck
import com.beezy.app.domain.repository.CardRepository
import com.beezy.app.domain.repository.DeckRepository
import com.beezy.app.domain.usecase.ExtractCardsFromDocumentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DecksViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val extractCardsUseCase: ExtractCardsFromDocumentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DecksUiState(isLoading = true))
    val uiState: StateFlow<DecksUiState> = _uiState.asStateFlow()

    init {
        observeDecks()
    }

    private fun observeDecks() {
        viewModelScope.launch {
            deckRepository.getDecksStream().collect { decks ->
                _uiState.update { current ->
                    val allTags = decks.flatMap { it.tags }.distinct().sorted()
                    val filtered = applyFilters(decks, current.searchQuery, current.selectedTag, current.sortOption)
                    current.copy(
                        decks = decks,
                        filteredDecks = filtered,
                        allTags = allTags,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            val filtered = applyFilters(current.decks, query, current.selectedTag, current.sortOption)
            current.copy(searchQuery = query, filteredDecks = filtered)
        }
    }

    fun onTagSelected(tag: String?) {
        _uiState.update { current ->
            val newSelected = if (current.selectedTag == tag) null else tag
            val filtered = applyFilters(current.decks, current.searchQuery, newSelected, current.sortOption)
            current.copy(selectedTag = newSelected, filteredDecks = filtered)
        }
    }

    fun onSortOptionChanged(option: DeckSortOption) {
        _uiState.update { current ->
            val filtered = applyFilters(current.decks, current.searchQuery, current.selectedTag, option)
            current.copy(sortOption = option, filteredDecks = filtered)
        }
    }

    fun setCreateDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isCreateDialogOpen = open) }
    }

    fun toggleStar(deckId: String, currentStarred: Boolean) {
        viewModelScope.launch {
            deckRepository.toggleStar(deckId, !currentStarred)
        }
    }

    fun deleteDeck(deckId: String) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deckId)
        }
    }

    fun createDeckManual(title: String, description: String, tags: List<String>) {
        if (title.isBlank()) return
        viewModelScope.launch {
            deckRepository.createDeck(title, description, tags)
            setCreateDialogOpen(false)
        }
    }

    fun createDeckFromText(title: String, description: String, tags: List<String>, rawText: String) {
        if (title.isBlank() || rawText.isBlank()) return
        viewModelScope.launch {
            val deckId = deckRepository.createDeck(title, description, tags)
            val extracted = extractCardsUseCase(rawText, defaultTag = tags.firstOrNull() ?: "Notes")
            if (extracted.isNotEmpty()) {
                val cardPairs = extracted.map { it.question to it.answer }
                cardRepository.addCards(deckId, cardPairs, tag = tags.firstOrNull() ?: "Notes")
            }
            setCreateDialogOpen(false)
        }
    }

    private fun applyFilters(
        decks: List<Deck>,
        query: String,
        selectedTag: String?,
        sortOption: DeckSortOption
    ): List<Deck> {
        var list = decks

        // 1. Tag Filter
        if (!selectedTag.isNullOrBlank()) {
            list = list.filter { it.tags.contains(selectedTag) }
        }

        // 2. Search Query Filter (Title, Description, or Tags)
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { deck ->
                deck.title.lowercase().contains(q) ||
                        deck.description.lowercase().contains(q) ||
                        deck.tags.any { it.lowercase().contains(q) }
            }
        }

        // 3. Sorting (Star always stays prioritized on top if starred)
        list = when (sortOption) {
            DeckSortOption.NEWEST -> list.sortedWith(compareByDescending<Deck> { it.starred }.thenByDescending { it.createdAt })
            DeckSortOption.OLDEST -> list.sortedWith(compareByDescending<Deck> { it.starred }.thenBy { it.createdAt })
            DeckSortOption.TITLE -> list.sortedWith(compareByDescending<Deck> { it.starred }.thenBy { it.title.lowercase() })
            DeckSortOption.CARDS_COUNT -> list.sortedWith(compareByDescending<Deck> { it.starred }.thenByDescending { it.totalCards })
        }

        return list
    }
}
