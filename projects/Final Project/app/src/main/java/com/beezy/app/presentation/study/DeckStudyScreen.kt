package com.beezy.app.presentation.study

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezyTextField
import com.beezy.app.presentation.components.EmptyStateView
import com.beezy.app.presentation.components.ErrorBanner
import com.beezy.app.presentation.components.TagChip
import com.beezy.app.presentation.study.components.AddEditCardDialog
import com.beezy.app.presentation.study.components.CardListItem
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape
import com.beezy.app.presentation.theme.CardShape
import com.beezy.app.presentation.theme.GradeAgainColor
import com.beezy.app.presentation.theme.PillShape
import com.beezy.app.presentation.theme.neumorphic

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeckStudyScreen(
    viewModel: DeckStudyViewModel,
    onLaunchFlashcards: (String) -> Unit,
    onLaunchQuiz: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = BeezyTheme.colors
    val deck = state.deck
    var cardToDelete by remember { mutableStateOf<com.beezy.app.domain.model.Card?>(null) }

    if (state.isLoading || deck == null) {
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

    val cardTags = state.cards.mapNotNull { it.tag }.filter { it.isNotBlank() }.distinct()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Deck Overview Banner
        item {
            Box(
                modifier = Modifier
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
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = deck.title,
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (deck.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = deck.description,
                            color = colors.textSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }

                    if (deck.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            deck.tags.forEach { TagChip(tag = it) }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Maturity Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Deck Maturity (${deck.maturityPercentage}%)",
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${state.cards.size} Cards",
                            color = colors.accent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { deck.maturityPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = colors.accent,
                        trackColor = colors.surface2,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // Write failure banner
        state.errorMessage?.let { message ->
            item {
                ErrorBanner(
                    message = message,
                    onDismiss = { viewModel.clearError() }
                )
            }
        }

        // 2. Study Mode Launchers (Flashcard Mode + Quiz Mode)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Flashcard Mode Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CardShape)
                        .background(colors.surface)
                        .border(1.dp, colors.border, CardShape)
                        .neumorphic(
                            isDarkTheme = colors.isDark,
                            elevation = 4.dp,
                            shape = CardShape,
                            cornerRadius = 20.dp
                        )
                        .clickable(enabled = state.cards.isNotEmpty()) {
                            onLaunchFlashcards(deck.id)
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = "Flashcards",
                            tint = colors.accent,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Flashcards",
                            color = colors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.cards.size} Cards",
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Quiz Mode Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CardShape)
                        .background(colors.surface)
                        .border(1.dp, colors.border, CardShape)
                        .neumorphic(
                            isDarkTheme = colors.isDark,
                            elevation = 4.dp,
                            shape = CardShape,
                            cornerRadius = 20.dp
                        )
                        .clickable(enabled = state.cards.isNotEmpty()) {
                            onLaunchQuiz(deck.id)
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = "Quiz Mode",
                            tint = com.beezy.app.presentation.theme.GradeGoodColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Quiz Mode",
                            color = colors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "4-Choice Test",
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 3. Cards Section Header with Add Card CTA
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cards (${state.cards.size})",
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                BeezyButton(
                    text = "Add Card",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    onClick = { viewModel.openAddCardDialog() }
                )
            }
        }

        // 4. Cards Search & Tag Filter
        item {
            Column {
                BeezyTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = "Search cards in this deck…",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                if (cardTags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TagChip(
                            tag = "All Tags",
                            isSelected = state.selectedCardTag == null,
                            onClick = { viewModel.onTagSelected(null) }
                        )
                        cardTags.forEach { tag ->
                            TagChip(
                                tag = tag,
                                isSelected = state.selectedCardTag == tag,
                                onClick = { viewModel.onTagSelected(tag) }
                            )
                        }
                    }
                }
            }
        }

        // 5. Cards List or Empty State
        if (state.filteredCards.isEmpty()) {
            item {
                EmptyStateView(
                    title = if (state.searchQuery.isNotBlank()) "No cards match your search" else "No cards in this deck yet",
                    subtitle = if (state.searchQuery.isNotBlank()) "Try another keyword" else "Tap '+ Add Card' to create your first question and answer pair.",
                    emoji = "📝"
                )
            }
        } else {
            items(state.filteredCards, key = { it.id }) { card ->
                CardListItem(
                    card = card,
                    onEdit = { viewModel.openEditCardDialog(card) },
                    onDelete = { cardToDelete = card }
                )
            }
        }
    }

    // Add / Edit Card Dialog
    if (state.isAddEditCardDialogOpen) {
        AddEditCardDialog(
            initialCard = state.editingCard,
            onDismiss = { viewModel.closeAddEditCardDialog() },
            onSave = { q, a, tag ->
                viewModel.saveCard(q, a, tag)
            }
        )
    }

    // Delete confirmation
    cardToDelete?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = {
                Text(
                    text = "Delete Card?",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "\"${card.question}\" and its review progress will be removed. This cannot be undone.",
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCard(card.id)
                        cardToDelete = null
                    }
                ) {
                    Text(text = "Delete", color = GradeAgainColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) {
                    Text(text = "Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface
        )
    }
}
