package com.beezy.app.data.local.dao

import com.beezy.app.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

interface CardDao {
    fun getCardsForDeckFlow(deckId: String): Flow<List<CardEntity>>
    suspend fun getCardsForDeck(deckId: String): List<CardEntity>
    suspend fun getCardById(id: String): CardEntity?
    suspend fun insertCard(card: CardEntity)
    suspend fun insertCards(cards: List<CardEntity>)
    suspend fun updateCard(card: CardEntity)
    suspend fun updateSM2State(
        id: String,
        reps: Int,
        interval: Int,
        easeFactor: Double,
        dueDate: Long,
        lastReviewed: Long
    )
    suspend fun deleteCard(id: String)
    suspend fun resetDeckProgress(deckId: String)
    suspend fun getAllCards(userId: String): List<CardEntity>
}
