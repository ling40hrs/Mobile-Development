package com.beezy.app.presentation.decks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.domain.model.Deck
import com.beezy.app.presentation.components.TagChip
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.CardShape
import com.beezy.app.presentation.theme.PillShape
import com.beezy.app.presentation.theme.neumorphic

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeckItemCard(
    deck: Deck,
    onClick: () -> Unit,
    onToggleStar: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BeezyTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(colors.surface)
            .border(1.dp, colors.border, CardShape)
            .neumorphic(
                isDarkTheme = colors.isDark,
                elevation = 6.dp,
                shape = CardShape,
                cornerRadius = 20.dp
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column {
            // Header Row: Title, Cards count badge, Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deck.title,
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Cards Count Badge
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(colors.surface2)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${deck.totalCards} cards",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Star Toggle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onToggleStar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (deck.starred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Star",
                        tint = if (deck.starred) BeezyAccent else colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Description
            if (deck.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = deck.description,
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            // Tag chips
            if (deck.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    deck.tags.forEach { tag ->
                        TagChip(tag = tag)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Maturity Progress Bar & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Maturity",
                            color = colors.textSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${deck.maturityPercentage}%",
                            color = colors.accent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { deck.maturityPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = colors.accent,
                        trackColor = colors.surface2,
                        strokeCap = StrokeCap.Round
                    )
                }

                if (!deck.builtIn) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Deck",
                            tint = colors.textSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
