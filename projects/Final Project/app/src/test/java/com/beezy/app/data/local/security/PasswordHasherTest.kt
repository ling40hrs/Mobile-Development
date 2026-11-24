package com.beezy.app.data.local.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.security.MessageDigest

class PasswordHasherTest {

    private lateinit var hasher: PasswordHasher

    @Before
    fun setUp() {
        hasher = PasswordHasher
    }

    @Test
    fun testHashAndVerify_roundTrip() {
        val stored = hasher.hash("password123")

        assertTrue(stored.startsWith("pbkdf2$"))
        assertTrue(hasher.verify("password123", stored))
        assertFalse(hasher.verify("password124", stored))
        assertFalse(hasher.needsUpgrade(stored))
    }

    @Test
    fun testHash_isSaltedPerCall() {
        val first = hasher.hash("same-password")
        val second = hasher.hash("same-password")

        assertNotEquals(first, second)
        assertTrue(hasher.verify("same-password", first))
        assertTrue(hasher.verify("same-password", second))
    }

    @Test
    fun testLegacySha256_isAcceptedAndFlaggedForUpgrade() {
        val legacy = MessageDigest.getInstance("SHA-256")
            .digest("password123".toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        assertTrue(hasher.needsUpgrade(legacy))
        assertTrue(hasher.verify("password123", legacy))
        assertFalse(hasher.verify("wrong-password", legacy))
    }

    @Test
    fun testMalformedHash_isRejected() {
        assertFalse(hasher.verify("password123", ""))
        assertFalse(hasher.verify("password123", "pbkdf2\$abc\$salt\$hash"))
        assertFalse(hasher.verify("password123", "not-a-real-hash"))
    }
}
