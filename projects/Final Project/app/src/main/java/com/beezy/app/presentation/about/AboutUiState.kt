package com.beezy.app.presentation.about

import com.beezy.app.data.local.datastore.ThemeMode
import com.beezy.app.domain.model.User

data class TeamMember(
    val name: String,
    val role: String = "Creator & Developer"
)

val TEAM_MEMBERS = listOf(
    TeamMember("Chrisleen Villacorza"),
    TeamMember("Aerovic Cudia"),
    TeamMember("Lloyd Casiano")
)

val AVATAR_OPTIONS = listOf(
    "⬡" to "Honeycomb",
    "🦉" to "Wise Owl",
    "🦊" to "Clever Fox",
    "🐻" to "Cozy Bear",
    "🚀" to "Explorer",
    "⭐" to "Super Star"
)

data class AboutUiState(
    val currentUser: User? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val selectedAvatarIndex: Int = 0,
    val isEditProfileDialogOpen: Boolean = false,
    val isResetDialogOpen: Boolean = false,
    val isDeleteAccountDialogOpen: Boolean = false,
    val isHelpDialogOpen: Boolean = false
)
