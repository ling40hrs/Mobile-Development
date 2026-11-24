package com.beezy.app.presentation.flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.components.EmptyStateView
import com.beezy.app.presentation.components.ErrorBanner
import com.beezy.app.presentation.components.FlipCardSurface
import com.beezy.app.presentation.components.TagChip
import com.beezy.app.presentation.flashcard.components.RatingBar
import com.beezy.app.presentation.flashcard.components.SessionCompleteDialog
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape

@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = BeezyTheme.colors
    var showResetConfirm by remember { mutableStateOf(false) }

    if (state.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.bg),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colors.accent)
        }
        return
    }

    if (state.cards.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.bg),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateView(
                title = if (state.errorMessage != null) "Could not load this deck" else "No cards to study",
                subtitle = state.errorMessage
                    ?: "This deck is currently empty. Add cards first to start your review session.",
                actionText = "Back to Deck",
                onActionClick = onNavigateBack
            )
        }
        return
    }

    val currentCard = state.currentCard ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Controls Bar: Deck Title, Shuffle, Reset, Exit
            Column {
                state.errorMessage?.let { message ->
                    ErrorBanner(
                        message = message,
                        onDismiss = { viewModel.clearError() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.deck?.title ?: "Flashcards",
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Shuffle Toggle
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (state.isShuffled) colors.accent.copy(alpha = 0.2f) else colors.surface2)
                                .clickable { viewModel.shuffleDeck() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = if (state.isShuffled) colors.accent else colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Reset Progress
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.surface2)
                                .clickable { showResetConfirm = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset Progress",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Exit button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.surface2)
                                .clickable { onNavigateBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar & Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Card ${state.currentIndex + 1} of ${state.totalCards}",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (state.isFlipped) "Answer" else "Question",
                        color = colors.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val progress = (state.currentIndex + 1).toFloat() / state.totalCards
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = colors.accent,
                    trackColor = colors.surface2,
                    strokeCap = StrokeCap.Round
                )
            }

            // 2. Center: 3D Flip Card
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                FlipCardSurface(
                    isFlipped = state.isFlipped,
                    onFlip = { viewModel.toggleFlip() },
                    frontContent = {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (!currentCard.tag.isNullOrBlank()) {
                                    TagChip(tag = currentCard.tag)
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Text(
                                    text = "Reps: ${currentCard.reps}",
                                    color = colors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = currentCard.question,
                                color = colors.textPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            )

                            Text(
                                text = "👆 Tap card to reveal answer",
                                color = colors.textSecondary.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    backContent = {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = currentCard.question,
                                color = colors.textSecondary,
                                fontSize = 13.sp,
                                maxLines = 2,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = currentCard.answer,
                                color = colors.textPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            )

                            Text(
                                text = "Rate recall below to schedule next review",
                                color = colors.textSecondary.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }

            // 3. Bottom Controls: SM-2 Rating Bar (when flipped) OR Prev/Next Nav
            Column(modifier = Modifier.fillMaxWidth()) {
                if (state.isFlipped) {
                    RatingBar(
                        onRate = { grade -> viewModel.rateCard(grade) }
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Button
                        Box(
                            modifier = Modifier
                                .clip(ButtonShape)
                                .background(if (state.hasPrev) colors.surface2 else colors.surface2.copy(alpha = 0.4f))
                                .clickable(enabled = state.hasPrev) { viewModel.previousCard() }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous",
                                    tint = if (state.hasPrev) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Prev",
                                    color = if (state.hasPrev) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Flip Hint Button
                        Box(
                            modifier = Modifier
                                .clip(ButtonShape)
                                .background(colors.accent)
                                .clickable { viewModel.toggleFlip() }
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "Show Answer",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Next Button
                        Box(
                            modifier = Modifier
                                .clip(ButtonShape)
                                .background(if (state.hasNext) colors.surface2 else colors.surface2.copy(alpha = 0.4f))
                                .clickable(enabled = state.hasNext) { viewModel.nextCard() }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Next",
                                    color = if (state.hasNext) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next",
                                    tint = if (state.hasNext) colors.textPrimary else colors.textSecondary.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Session Complete Dialog
    if (state.isSessionComplete) {
        SessionCompleteDialog(
            totalReviewed = state.cardsReviewedThisSession,
            maturityPercentage = state.deck?.maturityPercentage ?: 0,
            onStudyAgain = { viewModel.restartSession() },
            onFinish = onNavigateBack
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = {
                Text(
                    text = "Reset Progress?",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Review counts, intervals, and due dates for every card in this deck will be cleared. This cannot be undone.",
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirm = false
                        viewModel.resetDeckReviewProgress()
                    }
                ) {
                    Text(text = "Reset", color = BeezyAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(text = "Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface
        )
    }
}
