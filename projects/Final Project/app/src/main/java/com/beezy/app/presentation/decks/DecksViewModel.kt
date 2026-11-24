package com.beezy.app.presentation.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beezy.app.domain.repository.CardRepository
import com.beezy.app.domain.repository.DeckRepository
import com.beezy.app.domain.usecase.ExtractCardsFromDocumentUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DecksViewModel(
    private val userId: String,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val extractCardsUseCase: ExtractCardsFromDocumentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DecksUiState(isLoading = true))
    val uiState: StateFlow<DecksUiState> = _uiState.asStateFlow()

    /** Guards a double-tapped Create button so one tap creates one deck. */
    private var isCreatingDeck = false

    init {
        observeDecks()
    }

    private fun observeDecks() {
        viewModelScope.launch {
            try {
                deckRepository.getDecksStream(userId).collect { decks ->
                    _uiState.update { current ->
                        val allTags = collectDeckTags(decks)
                        val filtered = filterDecks(decks, current.searchQuery, current.selectedTag, current.sortOption)
                        current.copy(
                            decks = decks,
                            filteredDecks = filtered,
                            allTags = allTags,
                            isLoading = false
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Could not load your decks. Please try again."
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            val filtered = filterDecks(current.decks, query, current.selectedTag, current.sortOption)
            current.copy(searchQuery = query, filteredDecks = filtered)
        }
    }

    fun onTagSelected(tag: String?) {
        _uiState.update { current ->
            val newSelected = if (current.selectedTag == tag) null else tag
            val filtered = filterDecks(current.decks, current.searchQuery, newSelected, current.sortOption)
            current.copy(selectedTag = newSelected, filteredDecks = filtered)
        }
    }

    fun onSortOptionChanged(option: DeckSortOption) {
        _uiState.update { current ->
            val filtered = filterDecks(current.decks, current.searchQuery, current.selectedTag, option)
            current.copy(sortOption = option, filteredDecks = filtered)
        }
    }

    fun setCreateDialogOpen(open: Boolean) {
        _uiState.update {
            it.copy(
                isCreateDialogOpen = open,
                errorMessage = if (open) null else it.errorMessage
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun toggleStar(deckId: String, currentStarred: Boolean) {
        viewModelScope.launch {
            try {
                deckRepository.toggleStar(deckId, userId, !currentStarred)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not update the deck. Please try again.") }
            }
        }
    }

    fun deleteDeck(deckId: String) {
        viewModelScope.launch {
            try {
                deckRepository.deleteDeck(deckId, userId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not delete the deck. Please try again.") }
            }
        }
    }

    fun updateDeck(id: String, title: String, description: String, tags: List<String>) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                deckRepository.updateDeck(id, userId, title, description, tags)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not save the deck. Please try again.") }
            }
        }
    }

    fun createDeckManual(title: String, description: String, tags: List<String>) {
        if (title.isBlank() || isCreatingDeck) return
        isCreatingDeck = true
        viewModelScope.launch {
            try {
                deckRepository.createDeck(userId, title, description, tags)
                setCreateDialogOpen(false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not create the deck. Please try again.") }
            } finally {
                isCreatingDeck = false
            }
        }
    }

    fun createDeckFromText(title: String, description: String, tags: List<String>, rawText: String) {
        if (title.isBlank() || rawText.isBlank() || isCreatingDeck) return
        isCreatingDeck = true
        viewModelScope.launch {
            try {
                val deckId = deckRepository.createDeck(userId, title, description, tags)
                val extracted = extractCardsUseCase(rawText, defaultTag = tags.firstOrNull() ?: "Notes")
                if (extracted.isNotEmpty()) {
                    val cardPairs = extracted.map { it.question to it.answer }
                    cardRepository.addCards(deckId, cardPairs, tag = tags.firstOrNull() ?: "Notes")
                }
                setCreateDialogOpen(false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not create the deck. Please try again.") }
            } finally {
                isCreatingDeck = false
            }
        }
    }
}
