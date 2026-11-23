package com.beezy.app.presentation.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neumorphic(
    isDarkTheme: Boolean = false,
    elevation: Dp = 6.dp,
    shape: Shape = CardShape,
    cornerRadius: Dp = 20.dp
): Modifier = this.drawBehind {
    val darkShadowColor = if (isDarkTheme) {
        Color.Black.copy(alpha = 0.45f)
    } else {
        Color(0xFF2C261E).copy(alpha = 0.08f)
    }

    val lightHighlightColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.03f)
    } else {
        Color.White.copy(alpha = 0.65f)
    }

    drawIntoCanvas { canvas ->
        val paintDark = Paint().asFrameworkPaint().apply {
            color = darkShadowColor.toArgb()
            setShadowLayer(
                elevation.toPx() * 1.5f,
                elevation.toPx() * 0.7f,
                elevation.toPx() * 0.7f,
                darkShadowColor.toArgb()
            )
        }

        val paintLight = Paint().asFrameworkPaint().apply {
            color = lightHighlightColor.toArgb()
            setShadowLayer(
                elevation.toPx() * 1.5f,
                -elevation.toPx() * 0.6f,
                -elevation.toPx() * 0.6f,
                lightHighlightColor.toArgb()
            )
        }

        val radiusPx = cornerRadius.toPx()
        
        // Draw top-left highlight
        canvas.nativeCanvas.drawRoundRect(
            0f, 0f, size.width, size.height,
            radiusPx, radiusPx, paintLight
        )

        // Draw bottom-right shadow
        canvas.nativeCanvas.drawRoundRect(
            0f, 0f, size.width, size.height,
            radiusPx, radiusPx, paintDark
        )
    }
}
