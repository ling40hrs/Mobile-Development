package com.beezy.app.presentation.flashcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.theme.ButtonShape
import com.beezy.app.presentation.theme.GradeAgainColor
import com.beezy.app.presentation.theme.GradeEasyColor
import com.beezy.app.presentation.theme.GradeGoodColor
import com.beezy.app.presentation.theme.GradeHardColor

@Composable
fun RatingBar(
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val ratings = listOf(
        Triple(1, "Again", GradeAgainColor),
        Triple(2, "Hard", GradeHardColor),
        Triple(3, "Good", GradeGoodColor),
        Triple(4, "Easy", GradeEasyColor)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ratings.forEach { (grade, label, color) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(ButtonShape)
                    .background(color)
                    .clickable { onRate(grade) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$grade",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
