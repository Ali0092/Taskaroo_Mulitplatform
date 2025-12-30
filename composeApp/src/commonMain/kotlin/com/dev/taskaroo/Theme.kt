/**
 * Theme definitions and color palette for Taskaroo application.
 *
 * Defines the color scheme used throughout the application including:
 * - Core background and primary colors
 * - Task priority-specific colors (Urgent, High, Medium, Low)
 * - Text and UI element colors
 *
 * All colors follow Material Design 3 principles with accessibility in mind.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo

import androidx.compose.ui.graphics.Color

/**
 * Core background colors
 */
/** Main background color for screens and surfaces */
val backgroundColor: Color = Color(0xFFF3F3F3)
/** Text and content color on background surfaces */
val onBackgroundColor: Color = Color(0xFF454545)

/**
 * Primary colors
 * Used for app branding, primary actions, and key UI elements
 */
/** Primary brand color (green tone) */
val primary: Color = Color(0xFF6B806B)
/** Darker variant of primary color for contrast */
val primaryColorVariant: Color = Color(0xFF4F634F)
/** Lighter variant of primary color for subtle elements */
val primaryLiteColorVariant: Color = Color(0xFFBCC9BC)
/** Text and icon color on primary-colored surfaces */
val onPrimary = Color(0xFFFFFFFF)
/** Background color for selected items */
val selectedItemColor = Color(0XFFF6F1E7)

/**
 * Priority colors for task management
 * Each priority level has a foreground and background color for visual distinction
 */
/** Text/icon color for urgent priority tasks (Red) */
val urgentPriorityColor = Color(0xFFD32F2F)
/** Background color for urgent priority task chips */
val urgentPriorityBackground = Color(0xFFFFEBEE)

/** Text/icon color for high priority tasks (Orange-Red) */
val highPriorityColor = Color(0xFFFF6F00)
/** Background color for high priority task chips */
val highPriorityBackground = Color(0xFFFFF3E0)

/** Text/icon color for medium priority tasks (Yellow) */
val mediumPriorityColor = Color(0xFFF9A825)
/** Background color for medium priority task chips */
val mediumPriorityBackground = Color(0xFFFFF8E1)

/** Text/icon color for low priority tasks (Gray) */
val lowPriorityColor = Color(0xFF757575)
/** Background color for low priority task chips */
val lowPriorityBackground = Color(0xFFFAFAFA)