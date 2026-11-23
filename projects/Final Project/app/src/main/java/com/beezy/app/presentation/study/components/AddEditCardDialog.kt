package com.beezy.app.presentation.study.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.domain.model.Card
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.components.BeezyTextField
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.CardShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCardDialog(
    initialCard: Card?,
    onDismiss: () -> Unit,
    onSave: (question: String, answer: String, tag: String?) -> Unit
) {
    val colors = BeezyTheme.colors

    var question by remember { mutableStateOf(initialCard?.question ?: "") }
    var answer by remember { mutableStateOf(initialCard?.answer ?: "") }
    var tag by remember { mutableStateOf(initialCard?.tag ?: "") }

    val isEditing = initialCard != null

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
                    text = if (isEditing) "Edit Card" else "Add New Card",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question
                Text(
                    text = "Question / Prompt *",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                BeezyTextField(
                    value = question,
                    onValueChange = { question = it },
                    placeholder = "e.g. What is the powerhouse of the cell?",
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Answer
                Text(
                    text = "Answer *",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                BeezyTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    placeholder = "e.g. Mitochondria",
                    singleLine = false,
                    minLines = 2,
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Tag
                Text(
                    text = "Tag (optional)",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                BeezyTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    placeholder = "e.g. Organelles, Chapter 2"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
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
                    BeezyButton(
                        text = if (isEditing) "Save Changes" else "Add Card",
                        enabled = question.isNotBlank() && answer.isNotBlank(),
                        onClick = {
                            onSave(question, answer, tag.ifBlank { null })
                        }
                    )
                }
            }
        }
    }
}
