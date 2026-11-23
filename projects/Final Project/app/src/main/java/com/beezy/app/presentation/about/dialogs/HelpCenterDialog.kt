package com.beezy.app.presentation.about.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.CardShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterDialog(onDismiss: () -> Unit) {
    val colors = BeezyTheme.colors

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
                    text = "Help Center & FAQ 💡",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "How does Spaced Repetition work?",
                    color = colors.accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "When you rate a card during review (Again, Hard, Good, Easy), Beezy's SM-2 algorithm calculates when you are on the verge of forgetting it. Easy cards appear weeks later; difficult cards appear tomorrow.",
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "What is Deck Maturity?",
                    color = colors.accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A card is considered 'mature' when its scheduled recall interval reaches 21 days or longer. Your deck maturity bar shows what percentage of your cards are deeply encoded in long-term memory.",
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "How does Smart Notes import work?",
                    color = colors.accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "When creating a deck, switch to the 'Smart Notes' tab and paste your lecture notes. Our pattern extractor parses definitions, Q&A cues, and concepts to auto-generate up to 35 flashcards.",
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.align(Alignment.End)) {
                    BeezyButton(
                        text = "Got It",
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}
