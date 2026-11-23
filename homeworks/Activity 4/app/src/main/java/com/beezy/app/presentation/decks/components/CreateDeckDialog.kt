package com.beezy.app.presentation.decks.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.components.BeezyTextField
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape
import com.beezy.app.presentation.theme.CardShape
import com.beezy.app.presentation.theme.PillShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckDialog(
    onDismiss: () -> Unit,
    onCreateManual: (title: String, description: String, tags: List<String>) -> Unit,
    onCreateFromText: (title: String, description: String, tags: List<String>, rawText: String) -> Unit
) {
    val colors = BeezyTheme.colors
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Manual, 1 = Smart Import

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }
    var rawNotesText by remember { mutableStateOf("") }

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
                    text = "Create New Deck",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Switcher (Manual vs Smart Notes)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ButtonShape)
                        .background(colors.surface2)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(ButtonShape)
                            .background(if (selectedTab == 0) colors.accent else colors.surface2)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Manual",
                            color = if (selectedTab == 0) androidx.compose.ui.graphics.Color.White else colors.textSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(ButtonShape)
                            .background(if (selectedTab == 1) colors.accent else colors.surface2)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ Smart Notes / OCR",
                            color = if (selectedTab == 1) androidx.compose.ui.graphics.Color.White else colors.textSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Deck Title Input
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
                    placeholder = "e.g. Biology 101, JavaScript Fundamentals"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Deck Description Input
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

                // Tags Input
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

                if (selectedTab == 1) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Paste Notes / Lecture Text *",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Beezy will parse definitions, Q&A, and key concepts to generate up to 35 flashcards automatically.",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BeezyTextField(
                        value = rawNotesText,
                        onValueChange = { rawNotesText = it },
                        placeholder = "Paste your notes here…\n- Mitochondria: Powerhouse of the cell.\n- Q: What is osmosis? A: Movement of water across a membrane.",
                        singleLine = false,
                        minLines = 4,
                        maxLines = 8
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Cancel & Create
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BeezySecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    val isFormValid = if (selectedTab == 0) {
                        title.isNotBlank()
                    } else {
                        title.isNotBlank() && rawNotesText.isNotBlank()
                    }
                    BeezyButton(
                        text = if (selectedTab == 0) "Create Deck" else "Generate Cards",
                        enabled = isFormValid,
                        onClick = {
                            val tagsList = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            if (selectedTab == 0) {
                                onCreateManual(title, description, tagsList)
                            } else {
                                onCreateFromText(title, description, tagsList, rawNotesText)
                            }
                        }
                    )
                }
            }
        }
    }
}
