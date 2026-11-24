package com.beezy.app.data.repository

import com.beezy.app.data.local.BeezyDatabase
import com.beezy.app.data.local.entity.CardEntity
import com.beezy.app.domain.model.Card
import com.beezy.app.domain.model.SM2Result
import com.beezy.app.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CardRepositoryImpl(
    private val database: BeezyDatabase
) : CardRepository {

    private val cardDao = database.cardDao
    private val deckDao = database.deckDao

    override fun getCardsForDeckStream(deckId: String, userId: String): Flow<List<Card>> {
        return deckDao.getAllDecksFlow().map { decks ->
            decks.firstOrNull { it.deck.id == deckId && it.deck.userId == userId }
                ?.cards
                ?.map { it.toDomain() }
                ?: emptyList()
        }
    }

    override suspend fun getCardsForDeck(deckId: String, userId: String): List<Card> {
        if (!ownsDeck(deckId, userId)) return emptyList()
        return cardDao.getCardsForDeck(deckId).map { it.toDomain() }
    }

    override suspend fun getCardById(id: String): Card? {
        return cardDao.getCardById(id)?.toDomain()
    }

    override suspend fun addCard(deckId: String, question: String, answer: String, tag: String?): String {
        val id = "card-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(6)}"
        val entity = CardEntity(
            id = id,
            deckId = deckId,
            question = question.trim(),
            answer = answer.trim(),
            tag = tag?.trim()?.ifBlank { null },
            reps = 0,
            interval = 1,
            easeFactor = 2.5,
            dueDate = System.currentTimeMillis()
        )
        cardDao.insertCard(entity)
        return id
    }

    override suspend fun addCards(deckId: String, cards: List<Pair<String, String>>, tag: String) {
        val entities = cards.mapIndexed { index, (q, a) ->
            CardEntity(
                id = "card-${System.currentTimeMillis()}-$index-${UUID.randomUUID().toString().take(4)}",
                deckId = deckId,
                question = q.trim(),
                answer = a.trim(),
                tag = tag.trim().ifBlank { null },
                reps = 0,
                interval = 1,
                easeFactor = 2.5,
                dueDate = System.currentTimeMillis()
            )
        }
        cardDao.insertCards(entities)
    }

    override suspend fun updateCard(id: String, question: String, answer: String, tag: String?) {
        val current = cardDao.getCardById(id) ?: return
        val updated = current.copy(
            question = question.trim(),
            answer = answer.trim(),
            tag = tag?.trim()?.ifBlank { null }
        )
        cardDao.updateCard(updated)
    }

    override suspend fun updateCardSM2(id: String, sm2Result: SM2Result) {
        cardDao.updateSM2State(
            id = id,
            reps = sm2Result.reps,
            interval = sm2Result.interval,
            easeFactor = sm2Result.easeFactor,
            dueDate = sm2Result.dueDate,
            lastReviewed = System.currentTimeMillis()
        )
    }

    override suspend fun deleteCard(id: String) {
        cardDao.deleteCard(id)
    }

    override suspend fun resetDeckProgress(deckId: String, userId: String) {
        if (!ownsDeck(deckId, userId)) return
        cardDao.resetDeckProgress(deckId)
    }

    override suspend fun getAllCards(userId: String): List<Card> {
        return cardDao.getAllCards(userId).map { it.toDomain() }
    }

    private suspend fun ownsDeck(deckId: String, userId: String): Boolean {
        val deck = deckDao.getDeckById(deckId) ?: return false
        return deck.deck.userId == userId
    }

    private fun CardEntity.toDomain(): Card {
        return Card(
            id = id,
            deckId = deckId,
            question = question,
            answer = answer,
            tag = tag,
            reps = reps,
            interval = interval,
            easeFactor = easeFactor,
            dueDate = dueDate,
            lastReviewed = lastReviewed
        )
    }
}
