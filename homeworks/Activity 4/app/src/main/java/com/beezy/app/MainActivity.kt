package com.beezy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.beezy.app.data.local.datastore.ThemeMode
import com.beezy.app.presentation.navigation.BeezyNavGraph
import com.beezy.app.presentation.theme.BeezyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as BeezyApplication).container

        setContent {
            val userPreferences by appContainer.preferencesRepository.userPreferencesFlow
                .collectAsState(initial = null)

            val systemInDark = isSystemInDarkTheme()
            val isDarkTheme = when (userPreferences?.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                else -> systemInDark
            }

            val coroutineScope = rememberCoroutineScope()

            BeezyTheme(darkTheme = isDarkTheme) {
                BeezyNavGraph(
                    appContainer = appContainer,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = {
                        coroutineScope.launch {
                            val nextMode = if (isDarkTheme) ThemeMode.LIGHT else ThemeMode.DARK
                            appContainer.preferencesRepository.setThemeMode(nextMode)
                        }
                    }
                )
            }
        }
    }
}
