package com.beezy.app.domain.model

data class QuizQuestion(
    val cardId: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

data class SM2Result(
    val reps: Int,
    val interval: Int,
    val easeFactor: Double,
    val dueDate: Long
)

data class DocumentCardCandidate(
    val question: String,
    val answer: String,
    val tag: String = "Notes"
)
