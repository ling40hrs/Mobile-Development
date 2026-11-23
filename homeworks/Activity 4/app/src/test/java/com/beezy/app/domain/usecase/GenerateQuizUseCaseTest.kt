package com.beezy.app.domain.usecase

import com.beezy.app.domain.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GenerateQuizUseCaseTest {

    private lateinit var useCase: GenerateQuizUseCase

    @Before
    fun setUp() {
        useCase = GenerateQuizUseCase()
    }

    @Test
    fun testQuizGeneration_generatesFourOptionsAndCorrectIndexMatches() {
        val deckCards = listOf(
            Card(id = "1", deckId = "d1", question = "Capital of France?", answer = "Paris"),
            Card(id = "2", deckId = "d1", question = "Capital of Germany?", answer = "Berlin"),
            Card(id = "3", deckId = "d1", question = "Capital of Italy?", answer = "Rome"),
            Card(id = "4", deckId = "d1", question = "Capital of Spain?", answer = "Madrid")
        )

        val questions = useCase(deckCards)

        assertEquals(4, questions.size)

        for (q in questions) {
            assertEquals(4, q.options.size)
            assertTrue("Correct index should be in bounds 0..3", q.correctIndex in 0..3)
            val correctOption = q.options[q.correctIndex]
            val originalCard = deckCards.first { it.id == q.cardId }
            assertEquals(originalCard.answer, correctOption)
        }
    }
}
