package com.beezy.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "beezy_preferences")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val selectedAvatarIndex: Int = 0,
    val pomodoroFocusMinutes: Int = 25,
    val pomodoroShortBreakMinutes: Int = 5,
    val pomodoroLongBreakMinutes: Int = 15,
    val pomodoroCycles: Int = 4
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val AVATAR_INDEX = intPreferencesKey("avatar_index")
        val POMO_FOCUS = intPreferencesKey("pomo_focus_min")
        val POMO_SHORT = intPreferencesKey("pomo_short_min")
        val POMO_LONG = intPreferencesKey("pomo_long_min")
        val POMO_CYCLES = intPreferencesKey("pomo_cycles")
    }

    val currentUserIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CURRENT_USER_ID]
    }

    suspend fun setCurrentUserId(userId: String?) {
        context.dataStore.edit { preferences ->
            if (userId != null) {
                preferences[PreferencesKeys.CURRENT_USER_ID] = userId
            } else {
                preferences.remove(PreferencesKeys.CURRENT_USER_ID)
            }
        }
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val themeMode = try {
            ThemeMode.valueOf(themeModeStr)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
        val avatarIndex = preferences[PreferencesKeys.AVATAR_INDEX] ?: 0
        val pomoFocus = preferences[PreferencesKeys.POMO_FOCUS] ?: 25
        val pomoShort = preferences[PreferencesKeys.POMO_SHORT] ?: 5
        val pomoLong = preferences[PreferencesKeys.POMO_LONG] ?: 15
        val pomoCycles = preferences[PreferencesKeys.POMO_CYCLES] ?: 4

        UserPreferences(
            themeMode = themeMode,
            selectedAvatarIndex = avatarIndex,
            pomodoroFocusMinutes = pomoFocus,
            pomodoroShortBreakMinutes = pomoShort,
            pomodoroLongBreakMinutes = pomoLong,
            pomodoroCycles = pomoCycles
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setAvatarIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AVATAR_INDEX] = index
        }
    }

    suspend fun setPomodoroDurations(focus: Int, shortBreak: Int, longBreak: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.POMO_FOCUS] = focus
            preferences[PreferencesKeys.POMO_SHORT] = shortBreak
            preferences[PreferencesKeys.POMO_LONG] = longBreak
        }
    }
}
