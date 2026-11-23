package com.beezy.app.presentation.about.static

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.components.BeezyTopAppBar
import com.beezy.app.presentation.theme.BeezyTheme

@Composable
fun TermsOfServiceScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BeezyTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        BeezyTopAppBar(
            title = "Terms of Service",
            canNavigateBack = true,
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Terms of Use",
                color = colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Welcome to Beezy. By using this application, you agree to these terms:\n\n" +
                        "1. License & Usage: Beezy is provided free of charge for personal, educational study use.\n\n" +
                        "2. User Content: You are solely responsible for the study material, flashcards, and notes stored on your device.\n\n" +
                        "3. Disclaimer of Warranty: The application is provided 'as is' without warranty of any kind. We strive to provide a reliable, stable study environment.\n\n" +
                        "4. Study Science: Beezy implements standard SuperMemo SM-2 algorithms and the Pomodoro Technique to assist learning efficiency.",
                color = colors.textSecondary,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}
