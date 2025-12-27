package com.dev.taskaroo.database

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

@Composable
actual fun ProvideDatabaseHelper(content: @Composable () -> Unit) {
    val databaseHelper = remember {
        TaskDatabaseHelper(DatabaseDriverFactory().createDriver())
    }

    CompositionLocalProvider(LocalDatabase provides databaseHelper) {
        content()
    }
}
