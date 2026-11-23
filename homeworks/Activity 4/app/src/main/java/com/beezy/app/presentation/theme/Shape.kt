package com.beezy.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

val PillShape = RoundedCornerShape(999.dp)
val CardShape = RoundedCornerShape(20.dp)
val ButtonShape = RoundedCornerShape(14.dp)
val ModalShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
