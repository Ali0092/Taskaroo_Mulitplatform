package com.dev.taskaroo.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.NSUserDefaults

actual fun getPreferencesManager(): PreferencesManager = IosPreferencesManager()

class IosPreferencesManager : PreferencesManager {

    private val userDefaults = NSUserDefaults.standardUserDefaults
    private var _currentTheme = getCurrentThemeSync()
    private var _notificationsEnabled = getNotificationsEnabledSync()
    private val _settingsFlow = MutableStateFlow(AppSettings(_currentTheme, _notificationsEnabled))

    override val settingsFlow: StateFlow<AppSettings> = _settingsFlow

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        println("iOS: Updating theme to $themeMode")

        // Update persistent storage
        userDefaults.setObject(themeMode.name, "theme_mode")
        userDefaults.synchronize()

        // Update local theme
        _currentTheme = themeMode

        // Update StateFlow - single clean update
        _settingsFlow.value = AppSettings(themeMode, _notificationsEnabled)

        println("iOS: Theme updated successfully")
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        println("iOS: Updating notifications enabled to $enabled")

        // Update persistent storage
        userDefaults.setBool(enabled, "notifications_enabled")
        userDefaults.synchronize()

        // Update local state
        _notificationsEnabled = enabled

        // Update StateFlow
        _settingsFlow.value = AppSettings(_currentTheme, enabled)

        println("iOS: Notifications setting updated successfully")
    }

    override suspend fun getCurrentSettings(): AppSettings {
        return AppSettings(_currentTheme, _notificationsEnabled)
    }

    override fun onThemeChanged(callback: (ThemeMode) -> Unit) {
        // No-op: callback mechanism removed in favor of StateFlow
    }

    private fun getCurrentThemeSync(): ThemeMode {
        val themeModeString = userDefaults.stringForKey("theme_mode")
        return try {
            ThemeMode.valueOf(themeModeString ?: ThemeMode.LIGHT.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.LIGHT
        }
    }

    private fun getNotificationsEnabledSync(): Boolean {
        // Check if the key exists, if not return default true
        if (userDefaults.objectForKey("notifications_enabled") == null) {
            return true
        }
        return userDefaults.boolForKey("notifications_enabled")
    }
}