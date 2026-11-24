package com.beezy.app.presentation.pomodoro.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.pomodoro.PomodoroLimits
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.CardShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerSettingsDialog(
    focusMinutes: Int,
    shortBreakMinutes: Int,
    longBreakMinutes: Int,
    isRunning: Boolean,
    onDismiss: () -> Unit,
    onSave: (focus: Int, shortBreak: Int, longBreak: Int) -> Unit
) {
    val colors = BeezyTheme.colors

    var focus by remember { mutableIntStateOf(focusMinutes) }
    var shortBreak by remember { mutableIntStateOf(shortBreakMinutes) }
    var longBreak by remember { mutableIntStateOf(longBreakMinutes) }

    val hasChanges = focus != focusMinutes ||
        shortBreak != shortBreakMinutes ||
        longBreak != longBreakMinutes

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(colors.surface)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Timer Settings",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isRunning) {
                        "The running session keeps its time. New lengths apply after a reset or mode change."
                    } else {
                        "New lengths apply to the next session."
                    },
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                DurationStepper(
                    label = "Focus",
                    minutes = focus,
                    min = PomodoroLimits.FOCUS_MIN,
                    max = PomodoroLimits.FOCUS_MAX,
                    onChange = { focus = it }
                )
                Spacer(modifier = Modifier.height(14.dp))
                DurationStepper(
                    label = "Short break",
                    minutes = shortBreak,
                    min = PomodoroLimits.SHORT_BREAK_MIN,
                    max = PomodoroLimits.SHORT_BREAK_MAX,
                    onChange = { shortBreak = it }
                )
                Spacer(modifier = Modifier.height(14.dp))
                DurationStepper(
                    label = "Long break",
                    minutes = longBreak,
                    min = PomodoroLimits.LONG_BREAK_MIN,
                    max = PomodoroLimits.LONG_BREAK_MAX,
                    onChange = { longBreak = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BeezySecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BeezyButton(
                        text = "Save",
                        enabled = hasChanges,
                        onClick = { onSave(focus, shortBreak, longBreak) }
                    )
                }
            }
        }
    }
}
