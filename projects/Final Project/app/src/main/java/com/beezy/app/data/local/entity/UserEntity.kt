package com.beezy.app.data.local.entity

data class UserEntity(
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val displayName: String,
    val avatarIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
