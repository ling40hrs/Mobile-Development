package com.beezy.app.presentation.quiz

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.components.EmptyStateView
import com.beezy.app.presentation.quiz.components.QuizOptionButton
import com.beezy.app.presentation.quiz.components.QuizResultDialog
import com.beezy.app.presentation.theme.BeezyAccent
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.CardShape
import com.beezy.app.presentation.theme.neumorphic

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = BeezyTheme.colors

    if (state.isLoading) {
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

    if (state.questions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.bg),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateView(
                title = if (state.errorMessage != null) "Could not load this quiz" else "Not enough cards for quiz",
                subtitle = state.errorMessage
                    ?: "Quiz mode requires at least 1 card with question and answer. Add cards to this deck first.",
                actionText = "Back to Deck",
                onActionClick = onNavigateBack
            )
        }
        return
    }

    val currentQuestion = state.currentQuestion ?: return
    val prefixes = listOf("A", "B", "C", "D")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.deck?.title ?: "Quiz",
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.surface2)
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress & Score Readout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${state.currentIndex + 1} of ${state.totalQuestions}",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "SCORE: ${state.score}",
                        color = colors.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val progress = (state.currentIndex + 1).toFloat() / state.totalQuestions
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = colors.accent,
                    trackColor = colors.surface2,
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question Card
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
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "QUESTION",
                        color = colors.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQuestion.question,
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4 Multiple Choice Options
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = state.selectedOptionIndex == index
                    val isCorrect = currentQuestion.correctIndex == index

                    QuizOptionButton(
                        prefix = prefixes.getOrElse(index) { "${index + 1}" },
                        optionText = option,
                        isSelected = isSelected,
                        isCorrect = isCorrect,
                        isRevealed = state.isAnswerRevealed,
                        onClick = { viewModel.selectOption(index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Quiz Result Dialog
    if (state.isQuizComplete) {
        QuizResultDialog(
            score = state.score,
            total = state.totalQuestions,
            scorePercentage = state.scorePercentage,
            onTakeAgain = { viewModel.restartQuiz() },
            onBack = onNavigateBack
        )
    }
}
