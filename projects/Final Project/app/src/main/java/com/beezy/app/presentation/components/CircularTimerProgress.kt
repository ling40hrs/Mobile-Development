package com.beezy.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyTheme

@Composable
fun CircularTimerProgress(
    progress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    strokeWidth: Dp = 12.dp,
    progressColor: Color = BeezyAccent,
    centerContent: @Composable () -> Unit
) {
    val colors = BeezyTheme.colors
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 300),
        label = "CircularTimerProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = this.size.width - strokePx
            val topLeft = androidx.compose.ui.geometry.Offset(strokePx / 2f, strokePx / 2f)

            // Background Track
            drawArc(
                color = colors.surface2,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Animated Foreground Arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        centerContent()
    }
}
