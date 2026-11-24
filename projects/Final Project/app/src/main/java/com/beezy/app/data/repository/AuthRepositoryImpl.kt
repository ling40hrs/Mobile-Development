package com.beezy.app.data.repository

import com.beezy.app.data.local.BeezyDatabase
import com.beezy.app.data.local.datastore.UserPreferencesRepository
import com.beezy.app.data.local.entity.UserEntity
import com.beezy.app.data.local.security.PasswordHasher
import com.beezy.app.domain.model.User
import com.beezy.app.domain.repository.AuthRepository
import com.beezy.app.domain.validation.AuthValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.UUID

class AuthRepositoryImpl(
    private val database: BeezyDatabase,
    private val preferencesRepository: UserPreferencesRepository
) : AuthRepository {

    private val userDao = database.userDao

    /** Bumped after a profile write so observers re-read the row. */
    private val profileUpdates = MutableStateFlow(0)

    override val currentUserFlow: Flow<User?> =
        combine(preferencesRepository.currentUserIdFlow, profileUpdates) { userId, _ ->
            if (userId == null) null else readUserOrNull(userId)
        }

    /**
     * A session row that cannot be read falls back to the auth screen instead
     * of crashing the app at start-up; the stored session id is kept, so a
     * later launch with a healthy database signs the user back in.
     */
    private suspend fun readUserOrNull(userId: String): User? = try {
        userDao.getUserById(userId)?.toDomain()
    } catch (e: Exception) {
        null
    }

    override suspend fun getCurrentUser(): User? {
        val currentId = preferencesRepository.currentUserIdFlow.firstOrNull() ?: return null
        return userDao.getUserById(currentId)?.toDomain()
    }

    override suspend fun login(usernameOrEmail: String, password: String): Result<User> {
        AuthValidation.loginError(usernameOrEmail, password)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        val cleanIdentifier = AuthValidation.normalizeUsername(usernameOrEmail)

        val userEntity = userDao.getUserByUsernameOrEmail(cleanIdentifier)
            ?: return Result.failure(IllegalArgumentException(INVALID_CREDENTIALS))

        val isValid = withContext(Dispatchers.Default) {
            PasswordHasher.verify(password, userEntity.passwordHash)
        }
        if (!isValid) {
            return Result.failure(IllegalArgumentException(INVALID_CREDENTIALS))
        }

        if (PasswordHasher.needsUpgrade(userEntity.passwordHash)) {
            val upgraded = withContext(Dispatchers.Default) { PasswordHasher.hash(password) }
            userDao.updatePasswordHash(userEntity.id, upgraded)
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
        val cleanUser = AuthValidation.normalizeUsername(username)
        val cleanEmail = AuthValidation.normalizeEmail(email)
        val cleanName = displayName.trim().ifBlank { cleanUser }

        AuthValidation.signupError(cleanUser, cleanEmail, password, cleanName)?.let {
            return Result.failure(IllegalArgumentException(it))
        }

        val existing = userDao.getUserByUsernameOrEmail(cleanUser)
            ?: userDao.getUserByUsernameOrEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Username or email is already registered"))
        }

        val userId = "user-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(6)}"
        val passwordHash = withContext(Dispatchers.Default) { PasswordHasher.hash(password) }
        val entity = UserEntity(
            id = userId,
            username = cleanUser,
            email = cleanEmail,
            passwordHash = passwordHash,
            displayName = cleanName,
            avatarIndex = avatarIndex,
            createdAt = System.currentTimeMillis()
        )

        try {
            val rowId = userDao.insertUser(entity)
            if (rowId == -1L) {
                return Result.failure(IllegalStateException("Could not create the account. Please try again."))
            }

            database.seedStarterDecks(userId)
            preferencesRepository.setCurrentUserId(userId)
            preferencesRepository.setAvatarIndex(avatarIndex)
            return Result.success(entity.toDomain())
        } catch (_: Exception) {
            return Result.failure(IllegalStateException("Could not create the account. Please try again."))
        }
    }

    override suspend fun updateProfile(displayName: String, avatarIndex: Int): Result<User> {
        val currentUser = getCurrentUser()
            ?: return Result.failure(IllegalStateException("No user currently logged in"))

        val cleanName = displayName.trim().ifBlank { currentUser.username }
        userDao.updateUserProfile(currentUser.id, cleanName, avatarIndex)
        preferencesRepository.setAvatarIndex(avatarIndex)
        profileUpdates.update { it + 1 }

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

        database.purgeUserData(currentUser.id)
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

    private companion object {
        // Same message for an unknown account and a wrong password.
        const val INVALID_CREDENTIALS = "Incorrect username or password."
    }
}
