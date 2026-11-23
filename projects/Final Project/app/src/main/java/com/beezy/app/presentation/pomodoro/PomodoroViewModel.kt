package com.beezy.app.presentation.pomodoro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beezy.app.data.local.datastore.UserPreferencesRepository
import com.beezy.app.presentation.pomodoro.service.PomodoroForegroundService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PomodoroViewModel(
    private val context: Context,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { prefs ->
                if (!_uiState.value.isRunning) {
                    val defaultDuration = when (_uiState.value.currentMode) {
                        PomodoroMode.FOCUS -> prefs.pomodoroFocusMinutes * 60
                        PomodoroMode.SHORT_BREAK -> prefs.pomodoroShortBreakMinutes * 60
                        PomodoroMode.LONG_BREAK -> prefs.pomodoroLongBreakMinutes * 60
                    }
                    _uiState.update {
                        it.copy(
                            totalSeconds = defaultDuration,
                            remainingSeconds = defaultDuration,
                            targetCycles = prefs.pomodoroCycles
                        )
                    }
                }
            }
        }
    }

    fun switchMode(mode: PomodoroMode) {
        pauseTimer()
        val duration = mode.defaultMinutes * 60
        _uiState.update {
            it.copy(
                currentMode = mode,
                totalSeconds = duration,
                remainingSeconds = duration,
                isRunning = false
            )
        }
    }

    fun toggleStartPause() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _uiState.update { it.copy(isRunning = true) }
        PomodoroForegroundService.start(
            context,
            _uiState.value.currentMode.title,
            _uiState.value.formattedTime
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.isRunning) {
                delay(1000)
                val remaining = _uiState.value.remainingSeconds - 1
                _uiState.update { it.copy(remainingSeconds = remaining) }
                
                if (remaining % 5 == 0 || remaining <= 10) {
                    PomodoroForegroundService.update(
                        context,
                        _uiState.value.currentMode.title,
                        _uiState.value.formattedTime
                    )
                }
            }

            if (_uiState.value.remainingSeconds <= 0) {
                onTimerFinished()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
        PomodoroForegroundService.stop(context)
    }

    fun resetTimer() {
        pauseTimer()
        val defaultDuration = _uiState.value.currentMode.defaultMinutes * 60
        _uiState.update {
            it.copy(
                totalSeconds = defaultDuration,
                remainingSeconds = defaultDuration,
                isRunning = false
            )
        }
    }

    fun skipSession() {
        pauseTimer()
        advanceToNextMode()
    }

    private fun onTimerFinished() {
        pauseTimer()
        advanceToNextMode()
    }

    private fun advanceToNextMode() {
        val current = _uiState.value
        when (current.currentMode) {
            PomodoroMode.FOCUS -> {
                val newCycles = current.completedCycles + 1
                if (newCycles >= current.targetCycles) {
                    // Transition to Long Break
                    switchMode(PomodoroMode.LONG_BREAK)
                    _uiState.update { it.copy(completedCycles = 0) }
                } else {
                    // Transition to Short Break
                    switchMode(PomodoroMode.SHORT_BREAK)
                    _uiState.update { it.copy(completedCycles = newCycles) }
                }
            }
            PomodoroMode.SHORT_BREAK, PomodoroMode.LONG_BREAK -> {
                // Transition back to Focus
                switchMode(PomodoroMode.FOCUS)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        PomodoroForegroundService.stop(context)
    }
}
