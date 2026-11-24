package com.beezy.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.about.AVATAR_OPTIONS
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.components.BeezyTextField
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape
import com.beezy.app.presentation.theme.CardShape
import com.beezy.app.presentation.theme.GradeAgainColor
import com.beezy.app.presentation.theme.PillShape
import com.beezy.app.presentation.theme.neumorphic

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = BeezyTheme.colors

    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding
            Text(
                text = "🐝",
                fontSize = 52.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Beezy",
                color = colors.textPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Study made easy · Connected to Local Database",
                color = colors.textSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Auth Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(colors.surface)
                    .border(1.dp, colors.border, CardShape)
                    .neumorphic(
                        isDarkTheme = colors.isDark,
                        elevation = 8.dp,
                        shape = CardShape,
                        cornerRadius = 20.dp
                    )
                    .padding(24.dp)
            ) {
                Column {
                    // Segmented Mode Switcher (Sign In vs Create Account)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(PillShape)
                            .background(colors.surface2)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(PillShape)
                                .background(if (state.isLoginMode) colors.accent else Color.Transparent)
                            .clickable { viewModel.setMode(true) }
                            .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign In",
                                color = if (state.isLoginMode) Color.White else colors.textSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (state.isLoginMode) FontWeight.Bold else FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(PillShape)
                                .background(if (!state.isLoginMode) colors.accent else Color.Transparent)
                            .clickable { viewModel.setMode(false) }
                            .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Create Account",
                                color = if (!state.isLoginMode) Color.White else colors.textSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (!state.isLoginMode) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Demo Credentials helper card
                    if (state.isLoginMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ButtonShape)
                                .background(colors.surface2)
                                .clickable { viewModel.fillDemoCredentials() }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "🔑 Demo Account Available",
                                        color = colors.accent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "User: demo  |  Pass: password123",
                                        color = colors.textSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "Auto-fill",
                                    color = colors.accent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Error Message Banner
                    if (state.errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ButtonShape)
                                .background(GradeAgainColor.copy(alpha = 0.15f))
                                .border(1.dp, GradeAgainColor.copy(alpha = 0.5f), ButtonShape)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = state.errorMessage ?: "",
                                color = GradeAgainColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Display Name (Create Account mode only)
                    if (!state.isLoginMode) {
                        Text(
                            text = "Display Name",
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BeezyTextField(
                            value = state.displayName,
                            onValueChange = { viewModel.onDisplayNameChanged(it) },
                            placeholder = "e.g. Alex Johnson"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Username or Email Input
                    Text(
                        text = if (state.isLoginMode) "Username or Email *" else "Username *",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BeezyTextField(
                        value = state.usernameOrEmail,
                        onValueChange = { viewModel.onUsernameOrEmailChanged(it) },
                        placeholder = if (state.isLoginMode) "Enter username or email" else "Choose a username (min 3 chars)"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input
                    Text(
                        text = "Password *",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BeezyTextField(
                        value = state.password,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        placeholder = if (state.isLoginMode) "Enter password" else "Min 8 characters",
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = colors.textSecondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { showPassword = !showPassword }
                            )
                        }
                    )

                    // Avatar Selection (Create Account mode only)
                    if (!state.isLoginMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Choose Your Study Avatar",
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AVATAR_OPTIONS.forEachIndexed { index, (emoji, _) ->
                                val isSelected = state.selectedAvatarIndex == index
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) colors.accent.copy(alpha = 0.25f) else colors.surface2)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) colors.accent else colors.border,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.onAvatarChanged(index) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit CTA Button
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = colors.accent)
                        }
                    } else {
                        BeezyButton(
                            text = if (state.isLoginMode) "Sign In to Beezy" else "Create Local Account",
                            enabled = state.usernameOrEmail.isNotBlank() && state.password.isNotBlank(),
                            onClick = { viewModel.submit(onAuthSuccess) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy note
            Text(
                text = "Accounts and decks stay on this device.",
                color = colors.textSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
