package com.beezy.app.domain.repository

import com.beezy.app.domain.model.Deck
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun getDecksStream(userId: String): Flow<List<Deck>>
    suspend fun getDeckById(id: String, userId: String): Deck?
    suspend fun createDeck(userId: String, title: String, description: String, tags: List<String>): String
    suspend fun updateDeck(id: String, userId: String, title: String, description: String, tags: List<String>)
    suspend fun toggleStar(id: String, userId: String, isStarred: Boolean)
    suspend fun deleteDeck(id: String, userId: String)
    suspend fun resetAllData(userId: String)
}
