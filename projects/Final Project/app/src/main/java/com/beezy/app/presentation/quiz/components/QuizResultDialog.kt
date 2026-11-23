package com.beezy.app.presentation.quiz.components

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.CardShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultDialog(
    score: Int,
    total: Int,
    scorePercentage: Int,
    onTakeAgain: () -> Unit,
    onBack: () -> Unit
) {
    val colors = BeezyTheme.colors

    val emoji = when {
        scorePercentage >= 90 -> "🏆"
        scorePercentage >= 70 -> "🌟"
        scorePercentage >= 50 -> "👍"
        else -> "💪"
    }

    val feedback = when {
        scorePercentage >= 90 -> "Mastery achieved! Outstanding recall."
        scorePercentage >= 70 -> "Great job! Your retention is solid."
        scorePercentage >= 50 -> "Good effort! A few more reviews will lock it in."
        else -> "Keep going! Active testing is the fastest way to build memory."
    }

    BasicAlertDialog(onDismissRequest = onBack) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(colors.surface)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = emoji,
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Quiz Complete!",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Score: $score / $total ($scorePercentage%)",
                    color = colors.accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = feedback,
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BeezySecondaryButton(
                        text = "Back to Deck",
                        onClick = onBack
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BeezyButton(
                        text = "Take Again",
                        onClick = onTakeAgain
                    )
                }
            }
        }
    }
}
