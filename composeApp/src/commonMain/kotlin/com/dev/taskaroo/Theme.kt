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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dev.taskaroo.preferences.ThemeMode

/**
 * Core background colors
 */
/** Main background color for screens and surfaces */
val backgroundColorLite: Color = Color(0xFFF3F3F3)
val backgroundColorDark: Color = Color(0xFF282828)
/** Text and content color on background surfaces */
val onBackgroundColorLite: Color = Color(0xFF454545)
val onBackgroundColorDark: Color = Color(0xFFF3F3F3)

val surfaceColorLite: Color = Color(0xFFFFFFFF)
val surfaceColorDark: Color = Color(0xFF3E3E3E)

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

/**
 * Task Status colors for completion tracking
 * Provides visual feedback for task completion status including overdue tasks
 */
/** Text/icon color for undone tasks (Gray) */
val undoneStatusColor = Color(0xFF9E9E9E)
/** Background color for undone status badge */
val undoneStatusBackground = Color(0xFFF5F5F5)

/** Text/icon color for completed tasks (Green) */
val completedStatusColor = Color(0xFF1C9521)
/** Background color for completed status badge */
val completedStatusBackground = Color(0xFFE8F5E9)

/** Text/icon color for overdue tasks (Red) */
val overdueStatusColor = Color(0xFFD32F2F)
/** Background color for overdue status badge */
val overdueStatusBackground = Color(0xFFFFEBEE)


private val darkColorScheme = darkColorScheme(
    primary = primaryColorVariant,
//    secondary = GenZSecondary,
//    tertiary = GenZAccent,
    background = backgroundColorDark,
    surface = surfaceColorDark,
    onBackground = onBackgroundColorDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black
)

private val lightColorScheme = lightColorScheme(
    primary = primary,
//    secondary = GenZSecondary,
//    tertiary = GenZAccent,
    background = backgroundColorLite,
    surface = surfaceColorLite,
    onBackground = onBackgroundColorLite,
    onSurface = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black
)


@Composable
fun TaskarooAppTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val darkTheme = themeMode == ThemeMode.DARK
    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}