package com.beezy.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezy.app.presentation.theme.PillShape
import com.beezy.app.presentation.theme.getTagColor

@Composable
fun TagChip(
    tag: String,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val baseColor = getTagColor(tag)
    val bgColor = if (isSelected) baseColor else baseColor.copy(alpha = 0.18f)
    val textColor = if (isSelected) Color.White else baseColor

    Box(
        modifier = modifier
            .clip(PillShape)
            .background(bgColor)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = tag,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
