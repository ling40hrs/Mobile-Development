package com.beezy.app.data.repository

import com.beezy.app.data.local.BeezyDatabase
import com.beezy.app.data.local.entity.DeckEntity
import com.beezy.app.domain.model.Deck
import com.beezy.app.domain.repository.DeckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DeckRepositoryImpl(
    private val database: BeezyDatabase
) : DeckRepository {

    private val deckDao = database.deckDao

    override fun getDecksStream(): Flow<List<Deck>> {
        return deckDao.getAllDecksFlow().map { list ->
            list.map { item ->
                Deck(
                    id = item.deck.id,
                    title = item.deck.title,
                    description = item.deck.description,
                    tags = item.deck.tags,
                    builtIn = item.deck.builtIn,
                    starred = item.deck.starred,
                    createdAt = item.deck.createdAt,
                    totalCards = item.totalCards,
                    maturityPercentage = item.maturityPercentage
                )
            }
        }
    }

    override suspend fun getDeckById(id: String): Deck? {
        val item = deckDao.getDeckById(id) ?: return null
        return Deck(
            id = item.deck.id,
            title = item.deck.title,
            description = item.deck.description,
            tags = item.deck.tags,
            builtIn = item.deck.builtIn,
            starred = item.deck.starred,
            createdAt = item.deck.createdAt,
            totalCards = item.totalCards,
            maturityPercentage = item.maturityPercentage
        )
    }

    override suspend fun createDeck(title: String, description: String, tags: List<String>): String {
        val id = "deck-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(6)}"
        val entity = DeckEntity(
            id = id,
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

    override suspend fun updateDeck(id: String, title: String, description: String, tags: List<String>) {
        val entity = DeckEntity(
            id = id,
            title = title.trim(),
            description = description.trim(),
            tags = tags.map { it.trim() }.filter { it.isNotEmpty() }
        )
        deckDao.updateDeck(entity)
    }

    override suspend fun toggleStar(id: String, isStarred: Boolean) {
        deckDao.toggleStar(id, isStarred)
    }

    override suspend fun deleteDeck(id: String) {
        deckDao.deleteDeck(id)
    }

    override suspend fun resetAllData() {
        database.resetDatabase()
    }
}
