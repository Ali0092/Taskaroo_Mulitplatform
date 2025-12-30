/**
 * iOS-specific database provider implementation.
 *
 * Creates the SQLDelight iOS native driver and provides it through CompositionLocal
 * for access throughout the app. Unlike Android, iOS doesn't require a context parameter.
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

/**
 * iOS implementation of database provider.
 * Creates the SQLDelight native driver for iOS platform without requiring a context.
 *
 * @param content The composable content that will have access to the database
 */
@Composable
actual fun ProvideDatabaseHelper(content: @Composable () -> Unit) {
    val databaseHelper = remember {
        TaskDatabaseHelper(DatabaseDriverFactory().createDriver())
    }

    CompositionLocalProvider(LocalDatabase provides databaseHelper) {
        content()
    }
}
