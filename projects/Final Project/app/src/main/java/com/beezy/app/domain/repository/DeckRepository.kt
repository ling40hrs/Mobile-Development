package com.beezy.app.domain.repository

import com.beezy.app.domain.model.Deck
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun getDecksStream(): Flow<List<Deck>>
    suspend fun getDeckById(id: String): Deck?
    suspend fun createDeck(title: String, description: String, tags: List<String>): String
    suspend fun updateDeck(id: String, title: String, description: String, tags: List<String>)
    suspend fun toggleStar(id: String, isStarred: Boolean)
    suspend fun deleteDeck(id: String)
    suspend fun resetAllData()
}
