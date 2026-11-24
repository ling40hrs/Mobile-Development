package com.beezy.app.presentation.flashcard.components

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
fun SessionCompleteDialog(
    totalReviewed: Int,
    maturityPercentage: Int,
    onStudyAgain: () -> Unit,
    onFinish: () -> Unit
) {
    val colors = BeezyTheme.colors

    BasicAlertDialog(onDismissRequest = onFinish) {
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
                    text = "🎉",
                    fontSize = 44.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Review Session Complete!",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You reviewed $totalReviewed ${if (totalReviewed == 1) "card" else "cards"}. Current deck maturity: $maturityPercentage%.",
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
                        onClick = onFinish
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BeezyButton(
                        text = "Study Again",
                        onClick = onStudyAgain
                    )
                }
            }
        }
    }
}
