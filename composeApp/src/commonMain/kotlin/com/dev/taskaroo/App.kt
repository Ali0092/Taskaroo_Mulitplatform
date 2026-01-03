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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.dev.taskaroo.database.ProvideDatabaseHelper
import com.dev.taskaroo.preferences.AppSettings
import com.dev.taskaroo.preferences.ThemeMode
import com.dev.taskaroo.preferences.getPreferencesManager
import com.dev.taskaroo.screens.IntroScreen
import com.dev.taskaroo.screens.MainScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.compose_multiplatform

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

    val preferencesManager = remember { getPreferencesManager() }
    var currentTheme by remember { mutableStateOf(ThemeMode.SYSTEM) }

    // Initial theme setup
    LaunchedEffect(Unit) {
        val initialSettings = preferencesManager.getCurrentSettings()
        currentTheme = initialSettings.themeMode
        preferencesManager.onThemeChanged { newTheme ->
            println("DictionaryApp: Theme change callback received: $newTheme")
            currentTheme = newTheme
        }
    }

    // Also observe StateFlow as backup
    val settings by preferencesManager.settingsFlow.collectAsState(AppSettings())
    LaunchedEffect(settings.themeMode) {
        if (settings.themeMode != currentTheme) {
            println("DictionaryApp: StateFlow theme changed to ${settings.themeMode}")
            currentTheme = settings.themeMode
        }
    }


    TaskarooAppTheme(
        themeMode = currentTheme
    ) {
        ProvideDatabaseHelper {
            Navigator(screen = MainScreen())
        }
    }
}

