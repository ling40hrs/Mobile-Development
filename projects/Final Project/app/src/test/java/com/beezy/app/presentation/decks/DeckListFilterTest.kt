package com.beezy.app.presentation.decks

import com.beezy.app.domain.model.Deck
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckListFilterTest {

    private fun deck(
        id: String,
        title: String,
        tags: List<String> = emptyList(),
        starred: Boolean = false,
        createdAt: Long = 0L,
        totalCards: Int = 0,
        description: String = ""
    ) = Deck(
        id = id,
        title = title,
        description = description,
        tags = tags,
        starred = starred,
        createdAt = createdAt,
        totalCards = totalCards
    )

    @Test
    fun tagFilter_keepsOnlyDecksWithTheTag() {
        val decks = listOf(deck("a", "Biology", tags = listOf("Science")), deck("b", "History"))

        val result = filterDecks(decks, "", "Science", DeckSortOption.NEWEST)

        assertEquals(listOf("a"), result.map { it.id })
    }

    @Test
    fun query_matchesTitleDescriptionAndTagIgnoringCase() {
        val decks = listOf(
            deck("title", "Biology 101"),
            deck("description", "Notes", description = "Krebs cycle"),
            deck("tag", "Lecture", tags = listOf("biology"))
        )

        assertEquals(listOf("title", "tag"), filterDecks(decks, "BIOLOGY", null, DeckSortOption.NEWEST).map { it.id })
        assertEquals(listOf("description"), filterDecks(decks, "krebs", null, DeckSortOption.NEWEST).map { it.id })
    }

    @Test
    fun sortOption_ordersWithinStarredFirst() {
        val decks = listOf(
            deck("old", "Alpha", createdAt = 1L),
            deck("new", "Beta", createdAt = 3L),
            deck("starred", "Gamma", starred = true, createdAt = 2L)
        )

        assertEquals(
            listOf("starred", "new", "old"),
            filterDecks(decks, "", null, DeckSortOption.NEWEST).map { it.id }
        )
        assertEquals(
            listOf("starred", "old", "new"),
            filterDecks(decks, "", null, DeckSortOption.OLDEST).map { it.id }
        )
        assertEquals(
            listOf("starred", "old", "new"),
            filterDecks(decks, "", null, DeckSortOption.TITLE).map { it.id }
        )
    }

    @Test
    fun cardsCountSort_putsTheLargestDeckFirst() {
        val decks = listOf(deck("small", "A", totalCards = 2), deck("large", "B", totalCards = 9))

        assertEquals(
            listOf("large", "small"),
            filterDecks(decks, "", null, DeckSortOption.CARDS_COUNT).map { it.id }
        )
    }

    @Test
    fun collectTags_deduplicatesAndSorts() {
        val decks = listOf(
            deck("a", "A", tags = listOf("Science", "Exam")),
            deck("b", "B", tags = listOf("Exam", "Biology"))
        )

        assertEquals(listOf("Biology", "Exam", "Science"), collectDeckTags(decks))
    }
}
