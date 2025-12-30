/**
 * Android-specific SQLDelight driver factory.
 *
 * Creates an AndroidSqliteDriver that uses Android's built-in SQLite database.
 * Requires an Android Context to access the app's database directory.
 *
 * The driver is configured with:
 * - Database schema from TaskDatabase.Schema
 * - Database name: "task.db"
 * - Storage location: Application's internal database directory
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Android implementation of database driver factory.
 *
 * @param context Android application context for database access
 */
actual class DatabaseDriverFactory(private val context: Context) {
    /**
     * Creates an Android SQLite driver for the task database.
     *
     * @return SqlDriver instance configured for Android platform
     */
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(TaskDatabase.Schema, context, "task.db")
    }
}
