/**
 * Android-specific database provider implementation.
 *
 * Creates the SQLDelight Android driver using the application context and
 * provides it through CompositionLocal for access throughout the app.
 *
 * The database helper is remembered across recompositions to maintain
 * a single instance throughout the app's lifecycle.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.database

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation of database provider.
 * Uses Android Context to create the SQLDelight driver for the Android platform.
 *
 * @param content The composable content that will have access to the database
 */
@Composable
actual fun ProvideDatabaseHelper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val databaseHelper = remember {
        TaskDatabaseHelper(DatabaseDriverFactory(context).createDriver())
    }

    CompositionLocalProvider(LocalDatabase provides databaseHelper) {
        content()
    }
}
