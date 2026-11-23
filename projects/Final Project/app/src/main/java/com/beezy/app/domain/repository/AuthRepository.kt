package com.beezy.app.domain.repository

import com.beezy.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserFlow: Flow<User?>
    suspend fun getCurrentUser(): User?
    suspend fun login(usernameOrEmail: String, password: String): Result<User>
    suspend fun signup(
        username: String,
        email: String,
        password: String,
        displayName: String,
        avatarIndex: Int = 0
    ): Result<User>
    suspend fun updateProfile(displayName: String, avatarIndex: Int): Result<User>
    suspend fun logout()
    suspend fun deleteAccount(): Result<Unit>
    suspend fun getAllUsers(): List<User>
}
