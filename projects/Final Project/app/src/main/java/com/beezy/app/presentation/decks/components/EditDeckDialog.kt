package com.beezy.app.presentation.decks.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.domain.model.Deck
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.components.BeezyTextField
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.CardShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckDialog(
    deck: Deck,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, tags: List<String>) -> Unit
) {
    val colors = BeezyTheme.colors

    var title by remember(deck.id) { mutableStateOf(deck.title) }
    var description by remember(deck.id) { mutableStateOf(deck.description) }
    var tagsInput by remember(deck.id) { mutableStateOf(deck.tags.joinToString(", ")) }

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
                    text = "Edit Deck",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Title *",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                BeezyTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Deck title"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Description",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                BeezyTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Brief overview of what this deck covers…"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tags (comma separated)",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                BeezyTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    placeholder = "Science, Exam, Chapter 1"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BeezySecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BeezyButton(
                        text = "Save Changes",
                        enabled = title.isNotBlank(),
                        onClick = {
                            val tagsList = tagsInput.split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                            onSave(title, description, tagsList)
                        }
                    )
                }
            }
        }
    }
}
