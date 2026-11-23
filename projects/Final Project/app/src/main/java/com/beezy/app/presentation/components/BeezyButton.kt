package com.beezy.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyAccentPressed
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape
import com.beezy.app.presentation.theme.PillShape
import com.beezy.app.presentation.theme.neumorphic

@Composable
fun BeezyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = BeezyAccent,
    textColor: Color = Color.White,
    icon: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = if (isPressed) 0.97f else 1f
    val bg = if (isPressed) BeezyAccentPressed else backgroundColor

    Box(
        modifier = modifier
            .scale(scale)
            .clip(ButtonShape)
            .background(if (enabled) bg else bg.copy(alpha = 0.5f))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BeezySecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.97f else 1f

    Box(
        modifier = modifier
            .scale(scale)
            .clip(ButtonShape)
            .background(BeezyTheme.colors.surface2)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = BeezyTheme.colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
