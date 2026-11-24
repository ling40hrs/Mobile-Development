package com.beezy.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beezy.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setMode(isLogin: Boolean) {
        _uiState.update {
            it.copy(
                isLoginMode = isLogin,
                errorMessage = null
            )
        }
    }

    fun onUsernameOrEmailChanged(value: String) {
        _uiState.update { it.copy(usernameOrEmail = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onDisplayNameChanged(value: String) {
        _uiState.update { it.copy(displayName = value, errorMessage = null) }
    }

    fun onAvatarChanged(index: Int) {
        _uiState.update { it.copy(selectedAvatarIndex = index) }
    }

    fun fillDemoCredentials() {
        _uiState.update {
            it.copy(
                isLoginMode = true,
                usernameOrEmail = "demo",
                password = "password123",
                errorMessage = null
            )
        }
    }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = if (state.isLoginMode) {
                authRepository.login(
                    usernameOrEmail = state.usernameOrEmail,
                    password = state.password
                )
            } else {
                authRepository.signup(
                    username = state.usernameOrEmail,
                    email = if (state.usernameOrEmail.contains("@")) state.usernameOrEmail else "${state.usernameOrEmail}@beezy.app",
                    password = state.password,
                    displayName = state.displayName.ifBlank { state.usernameOrEmail },
                    avatarIndex = state.selectedAvatarIndex
                )
            }

            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successUser = user,
                            errorMessage = null
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toUserMessage()
                        )
                    }
                }
            )
        }
    }

    /**
     * Only show messages that the repository wrote for the user. Anything else
     * (SQL failures, I/O) becomes a generic line so internals never surface.
     */
    private fun Throwable.toUserMessage(): String = when (this) {
        is IllegalArgumentException,
        is IllegalStateException -> message ?: GENERIC_ERROR
        else -> GENERIC_ERROR
    }

    private companion object {
        const val GENERIC_ERROR = "Something went wrong. Please try again."
    }
}
