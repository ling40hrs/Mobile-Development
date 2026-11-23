package com.beezy.app.presentation.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beezy.app.data.local.datastore.ThemeMode
import com.beezy.app.data.local.datastore.UserPreferencesRepository
import com.beezy.app.domain.repository.AuthRepository
import com.beezy.app.domain.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AboutViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val deckRepository: DeckRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { prefs ->
                _uiState.update {
                    it.copy(
                        themeMode = prefs.themeMode,
                        selectedAvatarIndex = prefs.selectedAvatarIndex
                    )
                }
            }
        }

        viewModelScope.launch {
            authRepository.currentUserFlow.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setAvatar(index: Int) {
        viewModelScope.launch {
            preferencesRepository.setAvatarIndex(index)
            val current = _uiState.value.currentUser
            if (current != null) {
                authRepository.updateProfile(current.displayName, index)
            }
        }
    }

    fun setEditProfileDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isEditProfileDialogOpen = open) }
    }

    fun updateProfile(displayName: String, avatarIndex: Int) {
        viewModelScope.launch {
            authRepository.updateProfile(displayName, avatarIndex)
            setEditProfileDialogOpen(false)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            authRepository.deleteAccount()
        }
    }

    fun setResetDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isResetDialogOpen = open) }
    }

    fun setHelpDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isHelpDialogOpen = open) }
    }

    fun setFeedbackDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isFeedbackDialogOpen = open, feedbackSent = false) }
    }

    fun sendFeedback(email: String, message: String) {
        _uiState.update { it.copy(feedbackSent = true) }
    }

    fun resetAllData() {
        viewModelScope.launch {
            deckRepository.resetAllData()
            setResetDialogOpen(false)
        }
    }
}
