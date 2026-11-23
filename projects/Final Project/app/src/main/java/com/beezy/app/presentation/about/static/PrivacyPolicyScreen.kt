package com.beezy.app.presentation.about.static

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun PrivacyPolicyScreen(
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
            title = "Privacy Policy",
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
                text = "Privacy by Design",
                color = colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Beezy is engineered from the ground up as a 100% local-first, privacy-respecting study companion.\n\n" +
                        "1. Local Storage Only: All of your flashcard decks, cards, study histories, and preference settings are stored strictly on your local device SQLite database. We do not transmit or sync your study data to external servers.\n\n" +
                        "2. Zero Analytics & Tracking: Beezy does not include third-party ad networks, telemetry trackers, or fingerprinting scripts. We believe your learning process is personal and private.\n\n" +
                        "3. On-Device OCR: When you import notes or textbook snapshots, all text extraction is performed entirely on your local device.\n\n" +
                        "4. Data Portability: You retain complete ownership of your study material at all times.",
                color = colors.textSecondary,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}
