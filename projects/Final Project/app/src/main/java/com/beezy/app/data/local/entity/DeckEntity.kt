package com.beezy.app.data.local.entity

data class DeckEntity(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val builtIn: Boolean = false,
    val starred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
