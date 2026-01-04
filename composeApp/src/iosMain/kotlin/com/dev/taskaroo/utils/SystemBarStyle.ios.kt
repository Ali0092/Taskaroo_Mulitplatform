/**
 * iOS-specific implementation for system bar styling.
 *
 * This is a no-op implementation as iOS handles status bar styling
 * differently through Info.plist and UIViewController properties.
 *
 * @author Muhammad Ali
 * @date 2026-01-05
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.utils

import androidx.compose.runtime.Composable

/**
 * No-op implementation for iOS.
 *
 * iOS status bar styling is typically handled through:
 * - Info.plist configuration
 * - UIViewController.preferredStatusBarStyle
 * - UIUserInterfaceStyle for dark mode support
 *
 * @param darkTheme Whether the app is currently in dark theme mode (unused on iOS)
 */
@Composable
actual fun SetupSystemBars(darkTheme: Boolean) {
    // No-op for iOS - status bar styling is handled differently
}
