package com.beezy.app.data.local.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hashes local account passwords with PBKDF2-HMAC-SHA256.
 *
 * Stored format: `pbkdf2$<iterations>$<salt>$<hash>`, salt and hash Base64
 * without padding. A 64-character hex value is a hash written by the earlier
 * SHA-256 implementation; [verify] still accepts it so existing accounts keep
 * working, and callers rewrite the row with [hash] afterwards.
 */
object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 120_000
    private const val SALT_BYTES = 16
    private const val KEY_BITS = 256
    private const val PREFIX = "pbkdf2"
    private const val LEGACY_HEX_LENGTH = 64

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val derived = derive(password, salt, ITERATIONS)
        return listOf(PREFIX, ITERATIONS.toString(), encode(salt), encode(derived)).joinToString("$")
    }

    fun verify(password: String, stored: String): Boolean {
        if (isLegacy(stored)) {
            val candidate = legacyHash(password)
            return MessageDigest.isEqual(candidate.toByteArray(), stored.lowercase().toByteArray())
        }

        val parts = stored.split('$')
        if (parts.size != 4 || parts[0] != PREFIX) return false
        val iterations = parts[1].toIntOrNull() ?: return false
        if (iterations <= 0) return false
        val salt = decode(parts[2]) ?: return false
        val expected = decode(parts[3]) ?: return false

        return MessageDigest.isEqual(derive(password, salt, iterations), expected)
    }

    fun needsUpgrade(stored: String): Boolean = isLegacy(stored)

    private fun isLegacy(stored: String): Boolean =
        stored.length == LEGACY_HEX_LENGTH && stored.all { it in "0123456789abcdefABCDEF" }

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)
        return try {
            SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun legacyHash(password: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    private fun encode(bytes: ByteArray): String =
        Base64.getEncoder().withoutPadding().encodeToString(bytes)

    private fun decode(value: String): ByteArray? = try {
        Base64.getDecoder().decode(value)
    } catch (e: IllegalArgumentException) {
        null
    }
}
