package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Novexa Design System - Color Palette
 *
 * Core Identity:
 * - Signature Royal Blue (#1E40AF)
 * - Deep Obsidian Slate Black (#0F172A)
 * - Clean Off-White / Crisp Neutral Canvas (#F8FAFC)
 */

// Primary Palette - Royal Blue family
val NovexaBlue = Color(0xFF1E40AF)              // Primary brand color
val NovexaBlueDark = Color(0xFF172554)          // Deep Navy / Midnight Blue
val NovexaBlueLight = Color(0xFF3B82F6)         // Electric Sky Blue
val NovexaBlueVibrant = Color(0xFF2563EB)       // Interactive hover / focus state
val NovexaBlueContainer = Color(0xFFEFF6FF)     // Subtle blue surface tint
val NovexaOnBlueContainer = Color(0xFF1E3A8A)   // High contrast text on blue container

// Secondary & Neutral Dark - Obsidian Slate family
val NovexaBlack = Color(0xFF0F172A)             // Primary dark / text / secondary buttons
val NovexaBlackDeep = Color(0xFF020617)         // Maximum contrast black
val NovexaDarkSurface = Color(0xFF1E293B)       // Elevated card surface in dark mode
val NovexaMutedBlack = Color(0xFF334155)        // Subdued border/divider on dark canvas
val NovexaSlateGray = Color(0xFF475569)         // Neutral boundary gray

// Accent & Status Colors
val NovexaAccentAmber = Color(0xFFD97706)       // Rating stars & low stock warnings
val NovexaAccentAmberContainer = Color(0xFFFEF3C7)
val NovexaOnAccentAmber = Color(0xFF92400E)

val NovexaAccentEmerald = Color(0xFF059669)     // In-stock, verified & savings badge
val NovexaAccentEmeraldContainer = Color(0xFFDCFCE7)
val NovexaOnAccentEmerald = Color(0xFF065F46)

val NovexaAccentRose = Color(0xFFE11D48)        // Out-of-stock, urgent discount & cancel
val NovexaAccentRoseContainer = Color(0xFFFFE4E6)
val NovexaOnAccentRose = Color(0xFF9F1239)

// Light Canvas & Surface Tokens
val NovexaBgLight = Color(0xFFF8FAFC)           // Clean off-white background
val NovexaSurfaceLight = Color(0xFFFFFFFF)      // Pure white card background
val NovexaCardBorder = Color(0xFFE2E8F0)        // Subtle hairline border
val NovexaCardBorderFocused = Color(0xFF93C5FD) // Active border
val NovexaTextPrimary = Color(0xFF0F172A)       // Primary readable typography
val NovexaTextSecondary = Color(0xFF64748B)     // Secondary metadata text
val NovexaTextMuted = Color(0xFF94A3B8)         // Tertiary placeholders & stamps

// Bangladesh MFS Gateway Brand Colors
val NovexaBkashPink = Color(0xFFD12053)
val NovexaBkashContainer = Color(0xFFFDF2F4)
val NovexaNagadOrange = Color(0xFFF7941D)
val NovexaNagadContainer = Color(0xFFFFF7ED)
val NovexaRocketPurple = Color(0xFF8C3494)
val NovexaRocketContainer = Color(0xFFFAF5FF)

// Brushes & Atmospheric Gradients
val NovexaHeroGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF1E40AF))
)
val NovexaCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))
)
val NovexaDarkCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
)
