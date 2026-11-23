package com.beezy.app.data.local.entity

data class CardEntity(
    val id: String,
    val deckId: String,
    val question: String,
    val answer: String,
    val tag: String? = null,
    val reps: Int = 0,
    val interval: Int = 1, // in days
    val easeFactor: Double = 2.5,
    val dueDate: Long? = null, // timestamp in millis
    val lastReviewed: Long? = null // timestamp in millis
)
