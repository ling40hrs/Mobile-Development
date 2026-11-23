package com.beezy.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.theme.BeezyTheme

@Composable
fun BeezyTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onToggleTheme: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    val colors = BeezyTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(width = 0.5.dp, color = colors.border)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canNavigateBack) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.surface2)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Text(
                    text = "🐝",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Text(
                text = title,
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (onToggleTheme != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.surface2)
                        .clickable { onToggleTheme() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = colors.accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            actions()
        }
    }
}
