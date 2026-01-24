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
        return AndroidSqliteDriver(
            schema = TaskDatabase.Schema,
            context = context,
            name = "task.db",
            callback = object : AndroidSqliteDriver.Callback(TaskDatabase.Schema) {
                override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onOpen(db)
                    // Check if isTaskDone column exists, if not add it
                    try {
                        db.query("SELECT isTaskDone FROM Task LIMIT 1").use { cursor ->
                            cursor.moveToFirst()
                        }
                    } catch (_: Exception) {
                        // Column doesn't exist, add it
                        db.execSQL("ALTER TABLE Task ADD COLUMN isTaskDone INTEGER NOT NULL DEFAULT 0")
                    }

                    // Check if isMeeting column exists, if not add it
                    try {
                        db.query("SELECT isMeeting FROM Task LIMIT 1").use { cursor ->
                            cursor.moveToFirst()
                        }
                    } catch (_: Exception) {
                        // Column doesn't exist, add it
                        db.execSQL("ALTER TABLE Task ADD COLUMN isMeeting INTEGER NOT NULL DEFAULT 0")
                    }

                    // Check if meetingLink column exists, if not add it
                    try {
                        db.query("SELECT meetingLink FROM Task LIMIT 1").use { cursor ->
                            cursor.moveToFirst()
                        }
                    } catch (_: Exception) {
                        // Column doesn't exist, add it
                        db.execSQL("ALTER TABLE Task ADD COLUMN meetingLink TEXT NOT NULL DEFAULT ''")
                    }
                }
            }
        )
    }
}
