package com.beezy.app.domain.usecase

import com.beezy.app.domain.model.Card
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateMaturityUseCaseTest {

    private lateinit var useCase: CalculateMaturityUseCase

    @Before
    fun setUp() {
        useCase = CalculateMaturityUseCase()
    }

    @Test
    fun testEmptyDeck_returnsZeroMaturity() {
        val maturity = useCase(emptyList())
        assertEquals(0, maturity)
    }

    @Test
    fun testHalfMatureCards_returnsFiftyPercent() {
        val cards = listOf(
            createDummyCard("1", interval = 22), // mature (>= 21)
            createDummyCard("2", interval = 5),  // young
            createDummyCard("3", interval = 25), // mature (>= 21)
            createDummyCard("4", interval = 1)   // young
        )

        val maturity = useCase(cards)
        assertEquals(50, maturity)
    }

    @Test
    fun testAllMatureCards_returnsOneHundredPercent() {
        val cards = listOf(
            createDummyCard("1", interval = 21),
            createDummyCard("2", interval = 45)
        )

        val maturity = useCase(cards)
        assertEquals(100, maturity)
    }

    private fun createDummyCard(id: String, interval: Int): Card {
        return Card(
            id = id,
            deckId = "deck-1",
            question = "Q$id",
            answer = "A$id",
            interval = interval
        )
    }
}
