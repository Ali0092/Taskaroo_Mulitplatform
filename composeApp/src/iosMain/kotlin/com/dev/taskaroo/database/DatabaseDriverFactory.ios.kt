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
        val driver = NativeSqliteDriver(TaskDatabase.Schema, "task.db")

        // Migration: Add isTaskDone column if it doesn't exist
        // This handles databases created before the column was added
        try {
            driver.executeQuery(null, "SELECT isTaskDone FROM Task LIMIT 1", { cursor ->
                cursor.next()
            }, 0)
        } catch (_: Exception) {
            // Column doesn't exist, add it
            driver.execute(null, "ALTER TABLE Task ADD COLUMN isTaskDone INTEGER NOT NULL DEFAULT 0", 0)
        }

        return driver
    }
}
