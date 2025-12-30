/**
 * iOS-specific SQLDelight driver factory.
 *
 * Creates a NativeSqliteDriver that uses iOS native SQLite implementation.
 * Unlike Android, no context parameter is required as iOS uses direct file system access.
 *
 * The driver is configured with:
 * - Database schema from TaskDatabase.Schema
 * - Database name: "task.db"
 * - Storage location: Application's documents directory (automatic)
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS implementation of database driver factory.
 * No context parameter needed for iOS native driver.
 */
actual class DatabaseDriverFactory {
    /**
     * Creates an iOS native SQLite driver for the task database.
     *
     * @return SqlDriver instance configured for iOS platform
     */
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TaskDatabase.Schema, "task.db")
    }
}
