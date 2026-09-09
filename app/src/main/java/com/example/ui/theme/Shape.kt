package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Novexa Design System - Custom Component Shapes
 * Tailored for modern, tactile e-commerce cards, modals, and buttons.
 */
val NovexaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Specific component shapes for Novexa UI
val NovexaProductCardShape = RoundedCornerShape(14.dp)
val NovexaHeroCardShape = RoundedCornerShape(18.dp)
val NovexaButtonShape = RoundedCornerShape(10.dp)
val NovexaButtonShapeSmall = RoundedCornerShape(8.dp)
val NovexaPillShape = RoundedCornerShape(50)
val NovexaInputShape = RoundedCornerShape(10.dp)
val NovexaBadgeShape = RoundedCornerShape(6.dp)
val NovexaModalShape = RoundedCornerShape(20.dp)
val NovexaBottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
