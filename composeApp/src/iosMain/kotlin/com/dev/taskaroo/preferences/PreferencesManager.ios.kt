package com.dev.taskaroo.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.NSUserDefaults

actual fun getPreferencesManager(): PreferencesManager = IosPreferencesManager()

class IosPreferencesManager : PreferencesManager {

    private val userDefaults = NSUserDefaults.standardUserDefaults
    private var _currentTheme = getCurrentThemeSync()
    private var themeChangeCallback: ((ThemeMode) -> Unit)? = null
    private val _settingsFlow = MutableStateFlow(AppSettings(_currentTheme))

    override val settingsFlow: StateFlow<AppSettings> = _settingsFlow

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        println("iOS: Updating theme to $themeMode")

        // Update persistent storage
        userDefaults.setObject(themeMode.name, "theme_mode")
        userDefaults.synchronize()

        // Update local theme immediately
        _currentTheme = themeMode

        // Create new AppSettings and update StateFlow
        val newSettings = AppSettings(themeMode)
        _settingsFlow.value = newSettings

        // Force multiple updates to trigger recomposition
        _settingsFlow.value = newSettings.copy()
        _settingsFlow.value = newSettings

        // Call the callback for immediate UI update
        themeChangeCallback?.invoke(themeMode)

        println("iOS: Theme update complete, current flow value: ${_settingsFlow.value.themeMode}")
    }

    override suspend fun getCurrentSettings(): AppSettings {
        return AppSettings(_currentTheme)
    }

    override fun onThemeChanged(callback: (ThemeMode) -> Unit) {
        themeChangeCallback = callback
        println("iOS: Theme change callback registered")
    }

    private fun getCurrentThemeSync(): ThemeMode {
        val themeModeString = userDefaults.stringForKey("theme_mode")
        return try {
            ThemeMode.valueOf(themeModeString ?: ThemeMode.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }
}