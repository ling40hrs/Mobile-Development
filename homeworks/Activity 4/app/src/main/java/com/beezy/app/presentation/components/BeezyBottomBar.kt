package com.beezy.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.navigation.Screen
import com.beezy.app.presentation.theme.BeezyTheme

data class BottomBarItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val BOTTOM_BAR_ITEMS = listOf(
    BottomBarItem("Decks", Screen.Decks.route, Icons.Default.Style),
    BottomBarItem("Study", Screen.Study.route, Icons.AutoMirrored.Filled.MenuBook),
    BottomBarItem("Pomodoro", Screen.Pomodoro.route, Icons.Default.Timer),
    BottomBarItem("About", Screen.About.route, Icons.Default.Info)
)

@Composable
fun BeezyBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BeezyTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(width = 0.5.dp, color = colors.border)
            .navigationBarsPadding()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BOTTOM_BAR_ITEMS.forEach { item ->
                val isSelected = when {
                    item.route == Screen.Decks.route -> currentRoute == Screen.Decks.route
                    item.route == Screen.Study.route -> currentRoute?.startsWith("study") == true
                    item.route == Screen.Pomodoro.route -> currentRoute == Screen.Pomodoro.route
                    item.route == Screen.About.route -> currentRoute == Screen.About.route
                    else -> false
                }

                val itemColor = if (isSelected) colors.accent else colors.textSecondary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigate(item.route) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = itemColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = item.title,
                            color = itemColor,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
