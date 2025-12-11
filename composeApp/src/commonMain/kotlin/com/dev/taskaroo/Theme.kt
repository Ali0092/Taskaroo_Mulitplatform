package com.dev.taskaroo

import androidx.compose.ui.graphics.Color

// Organized theme colors data class
// Core background colors
val backgroundColor: Color = Color(0xFFF3F3F3)
val onBackgroundColor: Color = Color(0xFF454545)
// Primary colors
val primary: Color = Color(0xFF6B806B)
val primaryColorVariant: Color = Color(0xFF4F634F)
val primaryLiteColorVariant: Color = Color(0xFFBCC9BC)

val onPrimary = Color(0xFFFFFFFF)
val selectedItemColor = Color(0XFFF6F1E7)

// Priority colors
val urgentPriorityColor = Color(0xFFD32F2F) // Red
val urgentPriorityBackground = Color(0xFFFFEBEE)
val highPriorityColor = Color(0xFFFF6F00) // Orange-Red
val highPriorityBackground = Color(0xFFFFF3E0)
val mediumPriorityColor = Color(0xFFF9A825) // Yellow
val mediumPriorityBackground = Color(0xFFFFF8E1)
val lowPriorityColor = Color(0xFF757575) // Gray
val lowPriorityBackground = Color(0xFFFAFAFA)