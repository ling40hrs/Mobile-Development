package com.beezy.app.data.local.dao

import com.beezy.app.data.local.entity.DeckEntity
import com.beezy.app.data.local.entity.DeckWithCards
import kotlinx.coroutines.flow.Flow

interface DeckDao {
    fun getAllDecksFlow(): Flow<List<DeckWithCards>>
    suspend fun getDeckById(id: String): DeckWithCards?
    suspend fun insertDeck(deck: DeckEntity)
    suspend fun updateDeck(deck: DeckEntity)
    suspend fun toggleStar(id: String, userId: String, isStarred: Boolean)
    suspend fun deleteDeck(id: String, userId: String)
    suspend fun deleteAll(userId: String)
}
