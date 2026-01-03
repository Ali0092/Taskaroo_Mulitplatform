package com.dev.taskaroo.preferences


import kotlinx.coroutines.flow.StateFlow

interface PreferencesManager {
    val settingsFlow: StateFlow<AppSettings>
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun getCurrentSettings(): AppSettings
    fun onThemeChanged(callback: (ThemeMode) -> Unit)
}

expect fun getPreferencesManager(): PreferencesManager