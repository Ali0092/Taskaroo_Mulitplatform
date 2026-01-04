/**
 * Android-specific implementation for system bar styling.
 *
 * Uses Android's enableEdgeToEdge API with SystemBarStyle to dynamically
 * update status bar and navigation bar icon colors based on the current theme.
 *
 * @author Muhammad Ali
 * @date 2026-01-05
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Sets up Android system bars with appropriate icon colors based on theme.
 *
 * - Dark theme: Uses dark status bar background with light (white) icons
 * - Light theme: Uses light status bar background with dark (black) icons
 *
 * This function uses SideEffect to reactively update system bars whenever
 * the darkTheme parameter changes, ensuring immediate visual feedback.
 *
 * @param darkTheme Whether the app is currently in dark theme mode
 */
@Composable
actual fun SetupSystemBars(darkTheme: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        val activity = context.findActivity()
        if (activity is ComponentActivity) {
            activity.enableEdgeToEdge(
                statusBarStyle = if (darkTheme) {
                    // Dark theme: dark background with light (white) icons
                    SystemBarStyle.dark(
                        scrim = android.graphics.Color.TRANSPARENT
                    )
                } else {
                    // Light theme: light background with dark (black) icons
                    SystemBarStyle.light(
                        scrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT
                    )
                }
            )
        }
    }
}

/**
 * Helper extension function to find the Activity from a Context.
 *
 * Traverses the Context hierarchy to find the underlying Activity.
 * This is necessary because LocalContext may return an application
 * context or a wrapped context rather than the Activity directly.
 *
 * @return The Activity if found, null otherwise
 */
private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
