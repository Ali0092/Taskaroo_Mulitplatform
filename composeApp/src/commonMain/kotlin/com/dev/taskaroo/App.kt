/**
 * Main application entry point for Taskaroo task management application.
 *
 * This file contains the root composable function that sets up the application's
 * theme, database provider, and navigation system. It serves as the composition
 * root for the entire multiplatform application.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.dev.taskaroo.database.ProvideDatabaseHelper
import com.dev.taskaroo.screens.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * The root composable function of the Taskaroo application.
 *
 * Sets up the application's composition hierarchy including:
 * - Material Design 3 theming
 * - Database provider for SQLDelight access throughout the app
 * - Voyager navigation system with MainScreen as the initial screen
 *
 * This function is called from platform-specific entry points
 * (MainActivity on Android, MainViewController on iOS).
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        ProvideDatabaseHelper {
            Navigator(screen = MainScreen())
        }
    }
}

