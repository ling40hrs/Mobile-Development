package com.beezy.app.presentation.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beezy.app.domain.model.Card
import com.beezy.app.domain.repository.CardRepository
import com.beezy.app.domain.repository.DeckRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckStudyViewModel(
    private val deckId: String,
    private val userId: String,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeckStudyUiState())
    val uiState: StateFlow<DeckStudyUiState> = _uiState.asStateFlow()

    /** Guards a double-tapped Save button so one tap writes one card. */
    private var isSavingCard = false

    init {
        loadDeckAndCards()
    }

    private fun loadDeckAndCards() {
        viewModelScope.launch {
            try {
                val deck = deckRepository.getDeckById(deckId, userId)
                _uiState.update { it.copy(deck = deck) }

                cardRepository.getCardsForDeckStream(deckId, userId).collect { cards ->
                    _uiState.update { current ->
                        val filtered = applyFilters(cards, current.searchQuery, current.selectedCardTag)
                        val updatedDeck = current.deck?.copy(totalCards = cards.size)
                        current.copy(
                            deck = updatedDeck,
                            cards = cards,
                            filteredCards = filtered,
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
                        errorMessage = "Could not load this deck. Please try again."
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            val filtered = applyFilters(current.cards, query, current.selectedCardTag)
            current.copy(searchQuery = query, filteredCards = filtered)
        }
    }

    fun onTagSelected(tag: String?) {
        _uiState.update { current ->
            val newSelected = if (current.selectedCardTag == tag) null else tag
            val filtered = applyFilters(current.cards, current.searchQuery, newSelected)
            current.copy(selectedCardTag = newSelected, filteredCards = filtered)
        }
    }

    fun openAddCardDialog() {
        _uiState.update {
            it.copy(isAddEditCardDialogOpen = true, editingCard = null, errorMessage = null)
        }
    }

    fun openEditCardDialog(card: Card) {
        _uiState.update {
            it.copy(isAddEditCardDialogOpen = true, editingCard = card, errorMessage = null)
        }
    }

    fun closeAddEditCardDialog() {
        _uiState.update { it.copy(isAddEditCardDialogOpen = false, editingCard = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveCard(question: String, answer: String, tag: String?) {
        if (isSavingCard) return
        val editing = _uiState.value.editingCard
        isSavingCard = true
        viewModelScope.launch {
            try {
                if (editing == null) {
                    cardRepository.addCard(deckId, question, answer, tag)
                } else {
                    cardRepository.updateCard(editing.id, question, answer, tag)
                }
                closeAddEditCardDialog()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not save the card. Please try again.") }
            } finally {
                isSavingCard = false
            }
        }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            try {
                cardRepository.deleteCard(cardId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not delete the card. Please try again.") }
            }
        }
    }

    private fun applyFilters(
        cards: List<Card>,
        query: String,
        selectedTag: String?
    ): List<Card> {
        var list = cards
        if (!selectedTag.isNullOrBlank()) {
            list = list.filter { it.tag == selectedTag }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.question.lowercase().contains(q) ||
                        it.answer.lowercase().contains(q) ||
                        (it.tag?.lowercase()?.contains(q) == true)
            }
        }
        return list
    }
}
