package com.dev.taskaroo.database

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalDatabase = compositionLocalOf<TaskDatabaseHelper> {
    error("No database provided")
}

@Composable
expect fun ProvideDatabaseHelper(content: @Composable () -> Unit)
