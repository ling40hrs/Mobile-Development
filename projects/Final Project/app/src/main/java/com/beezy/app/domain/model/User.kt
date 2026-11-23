package com.beezy.app.domain.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val avatarIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
