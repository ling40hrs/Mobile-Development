package com.beezy.app.presentation.quiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape
import com.beezy.app.presentation.theme.GradeAgainColor
import com.beezy.app.presentation.theme.GradeGoodColor

@Composable
fun QuizOptionButton(
    prefix: String, // "A", "B", "C", "D"
    optionText: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isRevealed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BeezyTheme.colors

    val (bgColor, borderColor, textColor) = when {
        isRevealed && isCorrect -> {
            Triple(GradeGoodColor.copy(alpha = 0.2f), GradeGoodColor, GradeGoodColor)
        }
        isRevealed && isSelected && !isCorrect -> {
            Triple(GradeAgainColor.copy(alpha = 0.2f), GradeAgainColor, GradeAgainColor)
        }
        else -> {
            Triple(colors.surface, colors.border, colors.textPrimary)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(ButtonShape)
            .background(bgColor)
            .border(width = if (isRevealed && (isCorrect || isSelected)) 2.dp else 1.dp, color = borderColor, shape = ButtonShape)
            .clickable(enabled = !isRevealed) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option Prefix Badge (A, B, C, D)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isRevealed && isCorrect) GradeGoodColor else if (isRevealed && isSelected) GradeAgainColor else colors.surface2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = prefix,
                    color = if (isRevealed && (isCorrect || isSelected)) Color.White else colors.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = optionText,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isRevealed && isCorrect) FontWeight.Bold else FontWeight.Medium,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
