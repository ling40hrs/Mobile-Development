package com.beezy.app.presentation.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.domain.model.Deck
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.components.BeezyTextField
import com.beezy.app.presentation.components.EmptyStateView
import com.beezy.app.presentation.components.ErrorBanner
import com.beezy.app.presentation.components.TagChip
import com.beezy.app.presentation.decks.components.CreateDeckDialog
import com.beezy.app.presentation.decks.components.DeckItemCard
import com.beezy.app.presentation.decks.components.EditDeckDialog
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape
import com.beezy.app.presentation.theme.CardShape
import com.beezy.app.presentation.theme.PillShape
import com.beezy.app.presentation.theme.neumorphic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecksScreen(
    viewModel: DecksViewModel,
    onNavigateToDeck: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = BeezyTheme.colors

    var deckToDeleteId by remember { mutableStateOf<String?>(null) }
    var deckToEdit by remember { mutableStateOf<Deck?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search & Tag Filter Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Search Bar
                BeezyTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = "Search decks or tags…",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = colors.textSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.onSearchQueryChanged("") }
                            )
                        }
                    }
                )

                // Tag Filter Row
                if (state.allTags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TagChip(
                            tag = "All Decks",
                            isSelected = state.selectedTag == null,
                            onClick = { viewModel.onTagSelected(null) }
                        )

                        state.allTags.forEach { tag ->
                            TagChip(
                                tag = tag,
                                isSelected = state.selectedTag == tag,
                                onClick = { viewModel.onTagSelected(tag) }
                            )
                        }
                    }
                }

                // Sort Options Row
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort:",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    DeckSortOption.entries.forEach { option ->
                        val isSelected = state.sortOption == option
                        Box(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(if (isSelected) colors.accent else colors.surface2)
                                .clickable { viewModel.onSortOptionChanged(option) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = option.displayName,
                                color = if (isSelected) Color.White else colors.textSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Write failure banner
            state.errorMessage?.let { message ->
                ErrorBanner(
                    message = message,
                    onDismiss = { viewModel.clearError() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Deck List Content
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.accent)
                    }
                }

                state.filteredDecks.isEmpty() -> {
                    EmptyStateView(
                        title = if (state.searchQuery.isNotBlank() || state.selectedTag != null) "No matching decks found" else "No decks yet",
                        subtitle = if (state.searchQuery.isNotBlank() || state.selectedTag != null) "Try adjusting your search query or tag filter" else "Create your first deck or import study notes to begin your spaced repetition journey!",
                        emoji = if (state.searchQuery.isNotBlank()) "🔍" else "📚",
                        actionText = if (state.searchQuery.isBlank() && state.selectedTag == null) "+ Create First Deck" else null,
                        onActionClick = { viewModel.setCreateDialogOpen(true) },
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(state.filteredDecks, key = { it.id }) { deck ->
                            DeckItemCard(
                                deck = deck,
                                onClick = { onNavigateToDeck(deck.id) },
                                onToggleStar = { viewModel.toggleStar(deck.id, deck.starred) },
                                onEdit = { deckToEdit = deck },
                                onDelete = { deckToDeleteId = deck.id }
                            )
                        }
                    }
                }
            }
        }

        // Floating "+ New Deck" Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .background(colors.accent)
                    .neumorphic(
                        isDarkTheme = colors.isDark,
                        elevation = 8.dp,
                        shape = PillShape,
                        cornerRadius = 999.dp
                    )
                    .clickable { viewModel.setCreateDialogOpen(true) }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Deck",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "New Deck",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Create Deck Dialog
    if (state.isCreateDialogOpen) {
        CreateDeckDialog(
            onDismiss = { viewModel.setCreateDialogOpen(false) },
            onCreateManual = { title, desc, tags ->
                viewModel.createDeckManual(title, desc, tags)
            },
            onCreateFromText = { title, desc, tags, rawText ->
                viewModel.createDeckFromText(title, desc, tags, rawText)
            },
            errorMessage = state.errorMessage,
            onDismissError = { viewModel.clearError() }
        )
    }

    // Edit Deck Dialog
    deckToEdit?.let { deck ->
        EditDeckDialog(
            deck = deck,
            onDismiss = { deckToEdit = null },
            onSave = { title, desc, tags ->
                viewModel.updateDeck(deck.id, title, desc, tags)
                deckToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    deckToDeleteId?.let { id ->
        BasicAlertDialog(onDismissRequest = { deckToDeleteId = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(colors.surface)
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Delete Deck?",
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This will permanently delete this deck and all of its cards. This action cannot be undone.",
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        BeezySecondaryButton(
                            text = "Cancel",
                            onClick = { deckToDeleteId = null }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        BeezyButton(
                            text = "Delete",
                            backgroundColor = com.beezy.app.presentation.theme.GradeAgainColor,
                            onClick = {
                                viewModel.deleteDeck(id)
                                deckToDeleteId = null
                            }
                        )
                    }
                }
            }
        }
    }
}
