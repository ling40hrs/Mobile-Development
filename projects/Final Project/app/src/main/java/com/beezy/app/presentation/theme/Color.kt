package com.beezy.app.presentation.theme

import androidx.compose.ui.graphics.Color

// Light Mode Tokens (Warm Sand aesthetic)
val BeezyBgLight = Color(0xFFEDE8DF)
val BeezySurfaceLight = Color(0xFFE8E2D8)
val BeezySurface2Light = Color(0xFFDDD6CB)
val BeezyTextLight = Color(0xFF2C261E)
val BeezyTextSecondaryLight = Color(0xFF7A7062)
val BeezyBorderLight = Color(0xFFD5CDC1)

// Dark Mode Tokens (Deep Obsidian warm grey)
val BeezyBgDark = Color(0xFF1A1916)
val BeezySurfaceDark = Color(0xFF22201C)
val BeezySurface2Dark = Color(0xFF2C2A24)
val BeezyTextDark = Color(0xFFF0EDE6)
val BeezyTextSecondaryDark = Color(0xFF9E9689)
val BeezyBorderDark = Color(0xFF38352F)

// Honey Amber Accent
val BeezyAccent = Color(0xFFF5A623)
val BeezyAccentPressed = Color(0xFFE09518)
val BeezyAccentLight = Color(0xFFFFCC70)

// SM-2 Spaced Repetition Rating Palette
val GradeAgainColor = Color(0xFFD45C78) // Coral Red (Grade 1)
val GradeHardColor = Color(0xFFE8713C)  // Tangerine Orange (Grade 2)
val GradeGoodColor = Color(0xFF4DA87A)  // Emerald Green (Grade 3)
val GradeEasyColor = Color(0xFF2AACE2)  // Cerulean Blue (Grade 4)

// Deterministic Tag Palette (10 harmonious colors matched to PWA)
val TAG_PALETTE = listOf(
    Color(0xFFF5A623), // Amber
    Color(0xFF4DA87A), // Green
    Color(0xFF6A8DE0), // Blue
    Color(0xFFD45C78), // Coral
    Color(0xFF9468C4), // Purple
    Color(0xFF2AACE2), // Sky
    Color(0xFFE8713C), // Tangerine
    Color(0xFF3BB8A0), // Teal
    Color(0xFFC28F40), // Gold
    Color(0xFF6EA854)  // Lime
)

fun getTagColor(tag: String?): Color {
    if (tag.isNullOrBlank()) return TAG_PALETTE[0]
    var hash = 0
    for (ch in tag) {
        hash = ch.code + ((hash shl 5) - hash)
    }
    val index = kotlin.math.abs(hash) % TAG_PALETTE.size
    return TAG_PALETTE[index]
}
