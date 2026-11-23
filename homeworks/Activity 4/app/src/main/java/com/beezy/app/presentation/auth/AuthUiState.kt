package com.beezy.app.presentation.auth

import com.beezy.app.domain.model.User

data class AuthUiState(
    val isLoginMode: Boolean = true,
    val usernameOrEmail: String = "",
    val password: String = "",
    val displayName: String = "",
    val selectedAvatarIndex: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successUser: User? = null
)
