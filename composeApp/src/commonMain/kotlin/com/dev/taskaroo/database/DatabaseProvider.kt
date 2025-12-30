/**
 * Database provider using Compose CompositionLocal pattern.
 *
 * Provides database access throughout the composition hierarchy using the
 * expect/actual pattern for platform-specific database driver initialization.
 *
 * The LocalDatabase composition local allows any composable in the tree to
 * access the TaskDatabaseHelper instance without explicit parameter passing.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.database

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal for providing database access throughout the app.
 * Throws an error if accessed before being provided.
 */
val LocalDatabase = compositionLocalOf<TaskDatabaseHelper> {
    error("No database provided")
}

/**
 * Expected composable function that provides the database helper to the composition.
 * Platform-specific implementations (Android/iOS) create the appropriate SQLDelight driver.
 *
 * @param content The composable content that will have access to the database
 */
@Composable
expect fun ProvideDatabaseHelper(content: @Composable () -> Unit)
