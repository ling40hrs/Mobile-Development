package com.beezy.app.presentation.about

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.data.local.datastore.ThemeMode
import com.beezy.app.presentation.about.dialogs.DangerZoneResetDialog
import com.beezy.app.presentation.about.dialogs.DeleteAccountDialog
import com.beezy.app.presentation.about.dialogs.EditProfileDialog
import com.beezy.app.presentation.about.dialogs.HelpCenterDialog
import com.beezy.app.presentation.components.BeezyButton
import com.beezy.app.presentation.components.BeezySecondaryButton
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.ButtonShape
import com.beezy.app.presentation.theme.CardShape
import com.beezy.app.presentation.theme.GradeAgainColor
import com.beezy.app.presentation.theme.PillShape
import com.beezy.app.presentation.theme.neumorphic

@Composable
fun AboutScreen(
    viewModel: AboutViewModel,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = BeezyTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 1. App Header Card
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
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🐝",
                        fontSize = 44.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Beezy",
                        color = colors.textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Study made easy · Local-First Active Recall",
                        color = colors.textSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Active Database User Record (Read / Update / Delete)
            Text(
                text = "DATABASE ACCOUNT",
                color = colors.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(colors.surface)
                    .border(1.dp, colors.border, CardShape)
                    .padding(18.dp)
            ) {
                if (state.currentUser != null) {
                    val user = state.currentUser!!
                    val avatarEmoji = AVATAR_OPTIONS.getOrElse(user.avatarIndex) { AVATAR_OPTIONS[0] }.first
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(colors.accent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = avatarEmoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.displayName,
                                    color = colors.textPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "@${user.username} · ${user.email}",
                                    color = colors.textSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BeezySecondaryButton(
                                text = "Edit Profile",
                                onClick = { viewModel.setEditProfileDialogOpen(true) },
                                modifier = Modifier.weight(1f)
                            )
                            BeezySecondaryButton(
                                text = "Log Out",
                                onClick = { viewModel.logout() },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Avatar Selection Card
            Text(
                text = "STUDY AVATAR",
                color = colors.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AVATAR_OPTIONS.forEachIndexed { index, (emoji, name) ->
                    val isSelected = state.selectedAvatarIndex == index
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) colors.accent.copy(alpha = 0.25f) else colors.surface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) colors.accent else colors.border,
                                shape = CircleShape
                            )
                            .clickable { viewModel.setAvatar(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Theme Mode Selector
            Text(
                text = "APPEARANCE",
                color = colors.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ButtonShape)
                    .background(colors.surface2)
                    .padding(4.dp)
            ) {
                ThemeMode.entries.forEach { mode ->
                    val isSelected = state.themeMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(ButtonShape)
                            .background(if (isSelected) colors.accent else Color.Transparent)
                            .clickable { viewModel.setThemeMode(mode) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = if (isSelected) Color.White else colors.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Team Credits Card
            Text(
                text = "THE BEEZY TEAM",
                color = colors.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(colors.surface)
                    .border(1.dp, colors.border, CardShape)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TEAM_MEMBERS.forEach { member ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🐝", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = member.name,
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Help & Resources List
            Text(
                text = "RESOURCES & SUPPORT",
                color = colors.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(colors.surface)
                    .border(1.dp, colors.border, CardShape)
            ) {
                Column {
                    SettingsRow(
                        title = "Help Center & FAQ",
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        onClick = { viewModel.setHelpDialogOpen(true) }
                    )
                    HorizontalDivider(color = colors.border, thickness = 0.5.dp)
                    SettingsRow(
                        title = "Privacy Policy",
                        icon = Icons.Default.Policy,
                        onClick = onNavigateToPrivacy
                    )
                    HorizontalDivider(color = colors.border, thickness = 0.5.dp)
                    SettingsRow(
                        title = "Terms of Service",
                        icon = Icons.Default.Policy,
                        onClick = onNavigateToTerms
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 6. Danger Zone
            Text(
                text = "DANGER ZONE",
                color = GradeAgainColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(colors.surface)
                    .border(1.dp, GradeAgainColor.copy(alpha = 0.4f), CardShape)
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "Reset Database to Defaults",
                        color = colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Erases all user decks and cards and restores default sample decks.",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    BeezyButton(
                        text = "Reset All Data",
                        backgroundColor = GradeAgainColor,
                        onClick = { viewModel.setResetDialogOpen(true) }
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = colors.border, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Delete This Account",
                        color = colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Removes the signed-in account, its decks, and its review history from this device.",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    BeezySecondaryButton(
                        text = "Delete Account",
                        onClick = { viewModel.setDeleteAccountDialogOpen(true) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Dialogs
    if (state.isResetDialogOpen) {
        DangerZoneResetDialog(
            onDismiss = { viewModel.setResetDialogOpen(false) },
            onConfirmReset = { viewModel.resetAllData() }
        )
    }

    if (state.isDeleteAccountDialogOpen) {
        DeleteAccountDialog(
            onDismiss = { viewModel.setDeleteAccountDialogOpen(false) },
            onConfirmDelete = { viewModel.deleteAccount() }
        )
    }

    if (state.isHelpDialogOpen) {
        HelpCenterDialog(onDismiss = { viewModel.setHelpDialogOpen(false) })
    }

    if (state.isEditProfileDialogOpen && state.currentUser != null) {
        EditProfileDialog(
            currentUser = state.currentUser!!,
            onDismiss = { viewModel.setEditProfileDialogOpen(false) },
            onSave = { name, avatar -> viewModel.updateProfile(name, avatar) }
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val colors = BeezyTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = colors.textSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}
