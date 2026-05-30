package com.shahadat.streakhabittracker.util

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.InputStream

private val Context.dataStore by preferencesDataStore(name = "app_settings")

/**
 * Manages wallpaper images for the app background.
 * Copies selected images to internal storage for persistence.
 */
object WallpaperManager {

    private val KEY_WALLPAPER_PATH = stringPreferencesKey("wallpaper_path")
    private val KEY_HAS_WALLPAPER = booleanPreferencesKey("has_wallpaper")
    private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

    private const val WALLPAPER_FILENAME = "wallpaper.jpg"

    /**
     * Save a wallpaper image from a content URI to internal storage.
     */
    suspend fun setWallpaper(context: Context, uri: Uri) {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return
        val wallpaperFile = File(context.filesDir, WALLPAPER_FILENAME)

        wallpaperFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()

        context.dataStore.edit { prefs ->
            prefs[KEY_WALLPAPER_PATH] = wallpaperFile.absolutePath
            prefs[KEY_HAS_WALLPAPER] = true
        }
    }

    /**
     * Remove the wallpaper and return to default background.
     */
    suspend fun removeWallpaper(context: Context) {
        val wallpaperFile = File(context.filesDir, WALLPAPER_FILENAME)
        if (wallpaperFile.exists()) {
            wallpaperFile.delete()
        }

        context.dataStore.edit { prefs ->
            prefs.remove(KEY_WALLPAPER_PATH)
            prefs[KEY_HAS_WALLPAPER] = false
        }
    }

    /**
     * Get the wallpaper file path as a Flow.
     */
    fun getWallpaperPath(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            if (prefs[KEY_HAS_WALLPAPER] == true) {
                prefs[KEY_WALLPAPER_PATH]
            } else {
                null
            }
        }
    }

    /**
     * Check if a wallpaper is set.
     */
    fun hasWallpaper(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_HAS_WALLPAPER] == true
        }
    }

    /**
     * Get notifications enabled state.
     */
    fun getNotificationsEnabled(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_NOTIFICATIONS_ENABLED] != false // default: true
        }
    }

    /**
     * Set notifications enabled state.
     */
    suspend fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
