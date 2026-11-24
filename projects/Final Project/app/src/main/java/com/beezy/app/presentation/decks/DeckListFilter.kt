package com.beezy.app.presentation.decks

import com.beezy.app.domain.model.Deck

/** Tags across every deck, de-duplicated and sorted for the filter row. */
internal fun collectDeckTags(decks: List<Deck>): List<String> =
    decks.flatMap { it.tags }.distinct().sorted()

/**
 * Applies the tag filter, the search query, and the chosen sort. Starred decks
 * always sort ahead of unstarred ones.
 */
internal fun filterDecks(
    decks: List<Deck>,
    query: String,
    selectedTag: String?,
    sortOption: DeckSortOption
): List<Deck> {
    var list = decks

    if (!selectedTag.isNullOrBlank()) {
        list = list.filter { it.tags.contains(selectedTag) }
    }

    if (query.isNotBlank()) {
        val q = query.trim().lowercase()
        list = list.filter { deck ->
            deck.title.lowercase().contains(q) ||
                deck.description.lowercase().contains(q) ||
                deck.tags.any { it.lowercase().contains(q) }
        }
    }

    return when (sortOption) {
        DeckSortOption.NEWEST ->
            list.sortedWith(compareByDescending<Deck> { it.starred }.thenByDescending { it.createdAt })
        DeckSortOption.OLDEST ->
            list.sortedWith(compareByDescending<Deck> { it.starred }.thenBy { it.createdAt })
        DeckSortOption.TITLE ->
            list.sortedWith(compareByDescending<Deck> { it.starred }.thenBy { it.title.lowercase() })
        DeckSortOption.CARDS_COUNT ->
            list.sortedWith(compareByDescending<Deck> { it.starred }.thenByDescending { it.totalCards })
    }
}
