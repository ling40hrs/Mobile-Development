package com.beezy.app.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidationTest {

    @Test
    fun validSignup_returnsNoError() {
        val error = AuthValidation.signupError(
            username = "alex.johnson",
            email = "alex.johnson@example.com",
            password = "password123",
            displayName = "Alex Johnson"
        )
        assertNull(error)
    }

    @Test
    fun usernameTooShort_isRejected() {
        val error = AuthValidation.signupError("ab", "ab@beezy.app", "password123", "Ab")
        assertEquals(
            "Username must be 3–20 characters using letters, numbers, dots, dashes, or underscores",
            error
        )
    }

    @Test
    fun usernameWithInvalidCharacters_isRejected() {
        val error = AuthValidation.signupError("alex johnson", "alex@beezy.app", "password123", "Alex")
        assertEquals(
            "Username must be 3–20 characters using letters, numbers, dots, dashes, or underscores",
            error
        )
    }

    @Test
    fun passwordShorterThanEight_isRejected() {
        val error = AuthValidation.signupError("alex", "alex@beezy.app", "short7!", "Alex")
        assertEquals("Password must be at least 8 characters", error)
    }

    @Test
    fun malformedEmail_isRejected() {
        val error = AuthValidation.signupError("alex", "alex-at-example", "password123", "Alex")
        assertEquals("Please enter a valid email address", error)
    }

    @Test
    fun emailWithoutTld_isRejected() {
        val error = AuthValidation.signupError("alex", "alex@example", "password123", "Alex")
        assertEquals("Please enter a valid email address", error)
    }

    @Test
    fun displayNameOverFortyCharacters_isRejected() {
        val error = AuthValidation.signupError(
            "alex",
            "alex@beezy.app",
            "password123",
            "A".repeat(41)
        )
        assertEquals("Display name must be 40 characters or fewer", error)
    }

    @Test
    fun demoCredentials_passSignupRules() {
        assertNull(AuthValidation.signupError("demo", "demo@beezy.app", "password123", "Beezy Student"))
        assertNull(AuthValidation.loginError("demo", "password123"))
        assertNull(AuthValidation.loginError("demo@beezy.app", "password123"))
    }

    @Test
    fun loginWithBlankFields_isRejected() {
        assertEquals("Username or email cannot be empty", AuthValidation.loginError("  ", "password123"))
        assertEquals("Password cannot be empty", AuthValidation.loginError("demo", ""))
    }

    @Test
    fun loginDoesNotEnforceMinimumPasswordLength() {
        // Existing accounts keep working after the signup rule tightens.
        assertNull(AuthValidation.loginError("demo", "short6"))
    }

    @Test
    fun normalizationLowercasesAndTrims() {
        assertEquals("alex", AuthValidation.normalizeUsername("  Alex "))
        assertEquals("alex@example.com", AuthValidation.normalizeEmail(" Alex@Example.com "))
    }
}
