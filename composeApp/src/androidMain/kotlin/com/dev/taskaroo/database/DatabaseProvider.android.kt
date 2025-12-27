package com.dev.taskaroo.database

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

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
