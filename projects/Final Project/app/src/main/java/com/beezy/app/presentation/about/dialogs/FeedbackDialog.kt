package com.beezy.app.presentation.about.dialogs

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
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.components.BeezyTextField
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.CardShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSend: (email: String, message: String) -> Unit,
    feedbackSent: Boolean
) {
    val colors = BeezyTheme.colors

    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(colors.surface)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Send Feedback 💬",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (feedbackSent) {
                    Text(
                        text = "Thank you! Your feedback has been received.",
                        color = com.beezy.app.presentation.theme.GradeGoodColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.align(Alignment.End)) {
                        BeezyButton(
                            text = "Close",
                            onClick = onDismiss
                        )
                    }
                } else {
                    Text(
                        text = "Your Email (optional)",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BeezyTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "you@example.com"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Message *",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BeezyTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = "Share feature requests, bug reports, or study tips…",
                        singleLine = false,
                        minLines = 3,
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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
                            text = "Submit",
                            enabled = message.isNotBlank(),
                            onClick = { onSend(email, message) }
                        )
                    }
                }
            }
        }
    }
}
