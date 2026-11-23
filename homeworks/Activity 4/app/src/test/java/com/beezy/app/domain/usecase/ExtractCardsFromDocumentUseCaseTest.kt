package com.beezy.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExtractCardsFromDocumentUseCaseTest {

    private lateinit var useCase: ExtractCardsFromDocumentUseCase

    @Before
    fun setUp() {
        useCase = ExtractCardsFromDocumentUseCase()
    }

    @Test
    fun testExplicitQAPatternExtraction() {
        val sampleText = """
            Q: What is Active Recall?
            A: Actively retrieving facts from memory strengthens retention.
            
            Question: What is Interleaving?
            Answer: Mixing different topics within a study session.
        """.trimIndent()

        val cards = useCase(sampleText)

        assertEquals(2, cards.size)
        assertTrue(cards[0].question.contains("What is Active Recall"))
        assertTrue(cards[0].answer.contains("Actively retrieving facts"))
    }

    @Test
    fun testColonDefinitionPatternExtraction() {
        val sampleText = """
            Mitochondria: The powerhouse organelle responsible for ATP cellular respiration.
            Ribosome: The molecular machine that performs biological protein synthesis.
        """.trimIndent()

        val cards = useCase(sampleText)

        assertEquals(2, cards.size)
        assertEquals("What is Mitochondria?", cards[0].question)
        assertTrue(cards[0].answer.contains("powerhouse organelle"))
    }

    @Test
    fun testMaximumCardLimitIsRespected() {
        val lines = (1..50).joinToString("\n") { i ->
            "Term$i: This is the detailed scientific explanation for term number $i."
        }

        val cards = useCase(lines)

        assertTrue("Must not exceed 35 cards limit", cards.size <= 35)
    }
}
