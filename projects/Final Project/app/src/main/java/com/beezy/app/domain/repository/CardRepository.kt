package com.beezy.app.domain.repository

import com.beezy.app.domain.model.Card
import com.beezy.app.domain.model.SM2Result
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getCardsForDeckStream(deckId: String): Flow<List<Card>>
    suspend fun getCardsForDeck(deckId: String): List<Card>
    suspend fun getCardById(id: String): Card?
    suspend fun addCard(deckId: String, question: String, answer: String, tag: String?): String
    suspend fun addCards(deckId: String, cards: List<Pair<String, String>>, tag: String = "Notes")
    suspend fun updateCard(id: String, question: String, answer: String, tag: String?)
    suspend fun updateCardSM2(id: String, sm2Result: SM2Result)
    suspend fun deleteCard(id: String)
    suspend fun resetDeckProgress(deckId: String)
    suspend fun getAllCards(): List<Card>
}
