package com.beezy.app.domain.validation

/**
 * Format rules for local account credentials.
 *
 * Every function returns a user-facing message, or `null` when the input is
 * acceptable. Duplicate username/email checks live in the repository because
 * they need the database.
 */
object AuthValidation {

    private val USERNAME_PATTERN = Regex("^[a-z0-9._-]{3,20}$")
    private val EMAIL_PATTERN = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 64
    const val MAX_DISPLAY_NAME_LENGTH = 40

    fun normalizeUsername(username: String): String = username.trim().lowercase()

    fun normalizeEmail(email: String): String = email.trim().lowercase()

    fun signupError(
        username: String,
        email: String,
        password: String,
        displayName: String
    ): String? {
        usernameError(normalizeUsername(username))?.let { return it }
        emailError(normalizeEmail(email))?.let { return it }
        if (password.isBlank()) return "Password cannot be empty"
        if (password.length < MIN_PASSWORD_LENGTH) {
            return "Password must be at least $MIN_PASSWORD_LENGTH characters"
        }
        if (password.length > MAX_PASSWORD_LENGTH) {
            return "Password must be $MAX_PASSWORD_LENGTH characters or fewer"
        }
        if (displayName.trim().length > MAX_DISPLAY_NAME_LENGTH) {
            return "Display name must be $MAX_DISPLAY_NAME_LENGTH characters or fewer"
        }
        return null
    }

    fun loginError(identifier: String, password: String): String? {
        val clean = identifier.trim().lowercase()
        if (clean.isBlank()) return "Username or email cannot be empty"
        if (clean.contains("@")) {
            emailError(clean)?.let { return it }
        } else {
            usernameError(clean)?.let { return it }
        }
        if (password.isBlank()) return "Password cannot be empty"
        return null
    }

    private fun usernameError(username: String): String? = when {
        username.isBlank() -> "Username cannot be empty"
        !USERNAME_PATTERN.matches(username) ->
            "Username must be 3–20 characters using letters, numbers, dots, dashes, or underscores"
        else -> null
    }

    private fun emailError(email: String): String? = when {
        email.isBlank() -> "Email cannot be empty"
        !EMAIL_PATTERN.matches(email) -> "Please enter a valid email address"
        else -> null
    }
}
