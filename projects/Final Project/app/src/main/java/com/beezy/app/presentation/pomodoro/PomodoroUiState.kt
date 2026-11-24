package com.beezy.app.presentation.pomodoro

enum class PomodoroMode(val title: String, val defaultMinutes: Int) {
    FOCUS("Focus", 25),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15)
}

object PomodoroLimits {
    const val FOCUS_MIN = 5
    const val FOCUS_MAX = 90
    const val SHORT_BREAK_MIN = 5
    const val SHORT_BREAK_MAX = 30
    const val LONG_BREAK_MIN = 5
    const val LONG_BREAK_MAX = 60
    const val STEP = 5
}

data class PomodoroUiState(
    val currentMode: PomodoroMode = PomodoroMode.FOCUS,
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val completedCycles: Int = 0, // 0 to 4
    val targetCycles: Int = 4,
    val focusMinutes: Int = PomodoroMode.FOCUS.defaultMinutes,
    val shortBreakMinutes: Int = PomodoroMode.SHORT_BREAK.defaultMinutes,
    val longBreakMinutes: Int = PomodoroMode.LONG_BREAK.defaultMinutes,
    val isSettingsDialogOpen: Boolean = false
) {
    val progress: Float
        get() = if (totalSeconds == 0) 1f else 1f - (remainingSeconds.toFloat() / totalSeconds)

    val formattedTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}
