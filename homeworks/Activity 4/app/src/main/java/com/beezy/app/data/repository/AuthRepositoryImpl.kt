package com.beezy.app.data.repository

import com.beezy.app.data.local.BeezyDatabase
import com.beezy.app.data.local.datastore.UserPreferencesRepository
import com.beezy.app.data.local.entity.UserEntity
import com.beezy.app.domain.model.User
import com.beezy.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

class AuthRepositoryImpl(
    private val database: BeezyDatabase,
    private val preferencesRepository: UserPreferencesRepository
) : AuthRepository {

    private val userDao = database.userDao

    override val currentUserFlow: Flow<User?> = preferencesRepository.currentUserIdFlow.map { userId ->
        if (userId == null) null else userDao.getUserById(userId)?.toDomain()
    }

    override suspend fun getCurrentUser(): User? {
        val currentId = preferencesRepository.currentUserIdFlow.firstOrNull() ?: return null
        return userDao.getUserById(currentId)?.toDomain()
    }

    override suspend fun login(usernameOrEmail: String, password: String): Result<User> {
        val cleanIdentifier = usernameOrEmail.trim().lowercase()
        if (cleanIdentifier.isBlank()) {
            return Result.failure(IllegalArgumentException("Username or email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }

        val userEntity = userDao.getUserByUsernameOrEmail(cleanIdentifier)
            ?: return Result.failure(IllegalArgumentException("Account not found. Please sign up."))

        val hash = BeezyDatabase.hashPassword(password)
        if (userEntity.passwordHash != hash) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        // Set active session in preferences
        preferencesRepository.setCurrentUserId(userEntity.id)
        preferencesRepository.setAvatarIndex(userEntity.avatarIndex)

        return Result.success(userEntity.toDomain())
    }

    override suspend fun signup(
        username: String,
        email: String,
        password: String,
        displayName: String,
        avatarIndex: Int
    ): Result<User> {
        val cleanUser = username.trim().lowercase()
        val cleanEmail = email.trim().lowercase()
        val cleanName = displayName.trim().ifBlank { cleanUser }

        if (cleanUser.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters"))
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        val existing = userDao.getUserByUsernameOrEmail(cleanUser)
            ?: userDao.getUserByUsernameOrEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Username or email is already registered"))
        }

        val userId = "user-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(6)}"
        val entity = UserEntity(
            id = userId,
            username = cleanUser,
            email = cleanEmail,
            passwordHash = BeezyDatabase.hashPassword(password),
            displayName = cleanName,
            avatarIndex = avatarIndex,
            createdAt = System.currentTimeMillis()
        )

        try {
            val rowId = userDao.insertUser(entity)
            if (rowId == -1L) {
                return Result.failure(IllegalStateException("Database error while creating account"))
            }

            preferencesRepository.setCurrentUserId(userId)
            preferencesRepository.setAvatarIndex(avatarIndex)
            return Result.success(entity.toDomain())
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun updateProfile(displayName: String, avatarIndex: Int): Result<User> {
        val currentUser = getCurrentUser()
            ?: return Result.failure(IllegalStateException("No user currently logged in"))

        val cleanName = displayName.trim().ifBlank { currentUser.username }
        userDao.updateUserProfile(currentUser.id, cleanName, avatarIndex)
        preferencesRepository.setAvatarIndex(avatarIndex)

        val updated = userDao.getUserById(currentUser.id)?.toDomain()
            ?: return Result.failure(IllegalStateException("Failed to retrieve updated profile"))

        return Result.success(updated)
    }

    override suspend fun logout() {
        preferencesRepository.setCurrentUserId(null)
    }

    override suspend fun deleteAccount(): Result<Unit> {
        val currentUser = getCurrentUser()
            ?: return Result.failure(IllegalStateException("No active account to delete"))

        userDao.deleteUser(currentUser.id)
        preferencesRepository.setCurrentUserId(null)
        return Result.success(Unit)
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().map { it.toDomain() }
    }

    private fun UserEntity.toDomain(): User {
        return User(
            id = id,
            username = username,
            email = email,
            displayName = displayName,
            avatarIndex = avatarIndex,
            createdAt = createdAt
        )
    }
}
