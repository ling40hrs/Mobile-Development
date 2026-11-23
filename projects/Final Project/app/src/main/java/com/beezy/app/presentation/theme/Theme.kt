package com.beezy.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Immutable
data class BeezyCustomColors(
    val bg: Color,
    val surface: Color,
    val surface2: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val accent: Color,
    val accentPressed: Color,
    val isDark: Boolean
)

val LocalBeezyColors = staticCompositionLocalOf {
    BeezyCustomColors(
        bg = BeezyBgLight,
        surface = BeezySurfaceLight,
        surface2 = BeezySurface2Light,
        textPrimary = BeezyTextLight,
        textSecondary = BeezyTextSecondaryLight,
        border = BeezyBorderLight,
        accent = BeezyAccent,
        accentPressed = BeezyAccentPressed,
        isDark = false
    )
}

private val LightColorScheme = lightColorScheme(
    primary = BeezyAccent,
    onPrimary = Color.White,
    primaryContainer = BeezyAccentLight,
    onPrimaryContainer = BeezyTextLight,
    background = BeezyBgLight,
    onBackground = BeezyTextLight,
    surface = BeezySurfaceLight,
    onSurface = BeezyTextLight,
    surfaceVariant = BeezySurface2Light,
    onSurfaceVariant = BeezyTextSecondaryLight,
    outline = BeezyBorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BeezyAccent,
    onPrimary = Color.Black,
    primaryContainer = BeezyAccentPressed,
    onPrimaryContainer = BeezyTextDark,
    background = BeezyBgDark,
    onBackground = BeezyTextDark,
    surface = BeezySurfaceDark,
    onSurface = BeezyTextDark,
    surfaceVariant = BeezySurface2Dark,
    onSurfaceVariant = BeezyTextSecondaryDark,
    outline = BeezyBorderDark
)

@Composable
fun BeezyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val customColors = if (darkTheme) {
        BeezyCustomColors(
            bg = BeezyBgDark,
            surface = BeezySurfaceDark,
            surface2 = BeezySurface2Dark,
            textPrimary = BeezyTextDark,
            textSecondary = BeezyTextSecondaryDark,
            border = BeezyBorderDark,
            accent = BeezyAccent,
            accentPressed = BeezyAccentPressed,
            isDark = true
        )
    } else {
        BeezyCustomColors(
            bg = BeezyBgLight,
            surface = BeezySurfaceLight,
            surface2 = BeezySurface2Light,
            textPrimary = BeezyTextLight,
            textSecondary = BeezyTextSecondaryLight,
            border = BeezyBorderLight,
            accent = BeezyAccent,
            accentPressed = BeezyAccentPressed,
            isDark = false
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = customColors.surface.toArgb()
            window.navigationBarColor = customColors.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalBeezyColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object BeezyTheme {
    val colors: BeezyCustomColors
        @Composable
        get() = LocalBeezyColors.current
}
