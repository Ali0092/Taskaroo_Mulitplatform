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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import com.dev.taskaroo.database.ProvideDatabaseHelper
import com.dev.taskaroo.preferences.AppSettings
import com.dev.taskaroo.preferences.ThemeMode
import com.dev.taskaroo.preferences.getPreferencesManager
import com.dev.taskaroo.screens.MainScreen
import com.dev.taskaroo.utils.SetupSystemBars
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val preferencesManager = remember { getPreferencesManager() }
    val settings by preferencesManager.settingsFlow.collectAsState(AppSettings())

    val darkTheme = settings.themeMode == ThemeMode.DARK

    // Update system bars based on theme (Android only)
    SetupSystemBars(darkTheme = darkTheme)

    TaskarooAppTheme(themeMode = settings.themeMode) {
        ProvideDatabaseHelper {
            Navigator(screen = MainScreen())
        }
    }
}

