package com.beezy.app.domain.model

data class Deck(
    val id: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val builtIn: Boolean = false,
    val starred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val totalCards: Int = 0,
    val maturityPercentage: Int = 0
)

data class Card(
    val id: String,
    val deckId: String,
    val question: String,
    val answer: String,
    val tag: String? = null,
    val reps: Int = 0,
    val interval: Int = 1,
    val easeFactor: Double = 2.5,
    val dueDate: Long? = null,
    val lastReviewed: Long? = null
) {
    val isDue: Boolean
        get() = dueDate == null || dueDate <= System.currentTimeMillis()

    val isMature: Boolean
        get() = interval >= 21
}
