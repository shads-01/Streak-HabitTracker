package com.shahadat.streakhabittracker.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

/**
 * Security utilities for the locked vault feature.
 * Handles PIN/pattern hashing and EncryptedSharedPreferences.
 */
object SecurityUtils {

    private const val PREFS_NAME = "vault_prefs"
    private const val KEY_VAULT_HASH = "vault_hash"
    private const val KEY_LOCK_TYPE = "lock_type"  // "pin" or "pattern"
    private const val KEY_AUTO_LOCK = "auto_lock"  // "immediate", "1min", "5min"
    private const val KEY_VAULT_SETUP = "vault_setup"

    /**
     * SHA-256 hash of the input string.
     */
    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Get or create EncryptedSharedPreferences.
     */
    private fun getSecurePrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Check if vault has been set up (PIN or pattern configured).
     */
    fun isVaultSetup(context: Context): Boolean {
        return getSecurePrefs(context).getBoolean(KEY_VAULT_SETUP, false)
    }

    /**
     * Get the lock type ("pin" or "pattern").
     */
    fun getLockType(context: Context): String {
        return getSecurePrefs(context).getString(KEY_LOCK_TYPE, "pin") ?: "pin"
    }

    /**
     * Get auto-lock duration setting.
     */
    fun getAutoLockDuration(context: Context): String {
        return getSecurePrefs(context).getString(KEY_AUTO_LOCK, "immediate") ?: "immediate"
    }

    /**
     * Save the vault PIN or pattern hash.
     */
    fun setVaultCredential(context: Context, credential: String, lockType: String) {
        val hash = sha256(credential)
        getSecurePrefs(context).edit()
            .putString(KEY_VAULT_HASH, hash)
            .putString(KEY_LOCK_TYPE, lockType)
            .putBoolean(KEY_VAULT_SETUP, true)
            .apply()
    }

    /**
     * Verify the entered PIN/pattern against the stored hash.
     */
    fun verifyCredential(context: Context, credential: String): Boolean {
        val storedHash = getSecurePrefs(context).getString(KEY_VAULT_HASH, null) ?: return false
        return sha256(credential) == storedHash
    }

    /**
     * Update auto-lock duration setting.
     */
    fun setAutoLockDuration(context: Context, duration: String) {
        getSecurePrefs(context).edit()
            .putString(KEY_AUTO_LOCK, duration)
            .apply()
    }

    /**
     * Clear vault credentials (reset the vault).
     */
    fun clearVault(context: Context) {
        getSecurePrefs(context).edit()
            .remove(KEY_VAULT_HASH)
            .remove(KEY_LOCK_TYPE)
            .putBoolean(KEY_VAULT_SETUP, false)
            .apply()
    }

    /**
     * Get the auto-lock timeout in milliseconds.
     */
    fun getAutoLockTimeoutMs(context: Context): Long {
        return when (getAutoLockDuration(context)) {
            "immediate" -> 0L
            "1min" -> 60_000L
            "5min" -> 300_000L
            else -> 0L
        }
    }
}
