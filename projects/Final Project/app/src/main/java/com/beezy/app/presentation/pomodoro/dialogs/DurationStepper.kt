package com.beezy.app.presentation.pomodoro.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.pomodoro.PomodoroLimits
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape

@Composable
fun DurationStepper(
    label: String,
    minutes: Int,
    min: Int,
    max: Int,
    onChange: (Int) -> Unit
) {
    val colors = BeezyTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ButtonShape)
            .background(colors.surface2)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        StepperButton(
            enabled = minutes > min,
            contentDescription = "Decrease $label",
            isAdd = false,
            onClick = { onChange((minutes - PomodoroLimits.STEP).coerceAtLeast(min)) }
        )

        Text(
            text = "$minutes min",
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(64.dp),
            textAlign = TextAlign.Center
        )

        StepperButton(
            enabled = minutes < max,
            contentDescription = "Increase $label",
            isAdd = true,
            onClick = { onChange((minutes + PomodoroLimits.STEP).coerceAtMost(max)) }
        )
    }
}

@Composable
private fun StepperButton(
    enabled: Boolean,
    contentDescription: String,
    isAdd: Boolean,
    onClick: () -> Unit
) {
    val colors = BeezyTheme.colors
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (enabled) colors.accent else colors.surface)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isAdd) Icons.Default.Add else Icons.Default.Remove,
            contentDescription = contentDescription,
            tint = if (enabled) Color.White else colors.textSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}
