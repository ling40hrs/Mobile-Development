package com.beezy.app.data.repository

import com.beezy.app.data.local.BeezyDatabase
import com.beezy.app.data.local.entity.DeckEntity
import com.beezy.app.data.local.entity.DeckWithCards
import com.beezy.app.domain.model.Deck
import com.beezy.app.domain.repository.DeckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DeckRepositoryImpl(
    private val database: BeezyDatabase
) : DeckRepository {

    private val deckDao = database.deckDao

    override fun getDecksStream(userId: String): Flow<List<Deck>> {
        return deckDao.getAllDecksFlow().map { list ->
            list.filter { item -> item.deck.userId == userId }
                .map { item -> item.toDomain() }
        }
    }

    override suspend fun getDeckById(id: String, userId: String): Deck? {
        val item = deckDao.getDeckById(id) ?: return null
        if (item.deck.userId != userId) return null
        return item.toDomain()
    }

    override suspend fun createDeck(userId: String, title: String, description: String, tags: List<String>): String {
        val id = "deck-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(6)}"
        val entity = DeckEntity(
            id = id,
            userId = userId,
            title = title.trim(),
            description = description.trim(),
            tags = tags.map { it.trim() }.filter { it.isNotEmpty() },
            builtIn = false,
            starred = false,
            createdAt = System.currentTimeMillis()
        )
        deckDao.insertDeck(entity)
        return id
    }

    override suspend fun updateDeck(id: String, userId: String, title: String, description: String, tags: List<String>) {
        val entity = DeckEntity(
            id = id,
            userId = userId,
            title = title.trim(),
            description = description.trim(),
            tags = tags.map { it.trim() }.filter { it.isNotEmpty() }
        )
        deckDao.updateDeck(entity)
    }

    override suspend fun toggleStar(id: String, userId: String, isStarred: Boolean) {
        deckDao.toggleStar(id, userId, isStarred)
    }

    override suspend fun deleteDeck(id: String, userId: String) {
        deckDao.deleteDeck(id, userId)
    }

    override suspend fun resetAllData(userId: String) {
        database.resetDatabase(userId)
    }

    private fun DeckWithCards.toDomain(): Deck = Deck(
        id = deck.id,
        title = deck.title,
        description = deck.description,
        tags = deck.tags,
        builtIn = deck.builtIn,
        starred = deck.starred,
        createdAt = deck.createdAt,
        totalCards = totalCards,
        maturityPercentage = maturityPercentage
    )
}
