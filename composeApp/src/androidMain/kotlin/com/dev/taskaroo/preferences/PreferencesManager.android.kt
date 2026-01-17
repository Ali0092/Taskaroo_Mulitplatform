package com.dev.taskaroo.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual fun getPreferencesManager(): PreferencesManager {
    return AndroidPreferencesManager.instance
}

class AndroidPreferencesManager private constructor(
    private val sharedPreferences: SharedPreferences
) : PreferencesManager {

    companion object {
        @Volatile
        private var INSTANCE: AndroidPreferencesManager? = null

        val instance: AndroidPreferencesManager
            get() = INSTANCE ?: throw IllegalStateException("AndroidPreferencesManager not initialized. Call initialize() first.")

        fun initialize(context: Context): AndroidPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AndroidPreferencesManager(
                    context.getSharedPreferences("genz_dictionary_prefs", Context.MODE_PRIVATE)
                )
                INSTANCE = instance
                instance
            }
        }
    }

    private val _settingsFlow = MutableStateFlow(getCurrentSettingsSync())

    override val settingsFlow: StateFlow<AppSettings> = _settingsFlow

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        sharedPreferences.edit()
            .putString("theme_mode", themeMode.name)
            .apply()

        _settingsFlow.value = _settingsFlow.value.copy(themeMode = themeMode)
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean("notifications_enabled", enabled)
            .apply()

        _settingsFlow.value = _settingsFlow.value.copy(notificationsEnabled = enabled)
    }

    override suspend fun getCurrentSettings(): AppSettings {
        return getCurrentSettingsSync()
    }

    override fun onThemeChanged(callback: (ThemeMode) -> Unit) {
        // No-op: callback mechanism removed in favor of StateFlow
    }

    fun getCurrentSettingsSync(): AppSettings {
        val themeModeString = sharedPreferences.getString("theme_mode", ThemeMode.LIGHT.name)
        val themeMode = try {
            ThemeMode.valueOf(themeModeString ?: ThemeMode.LIGHT.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.LIGHT
        }

        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)

        return AppSettings(
            themeMode = themeMode,
            notificationsEnabled = notificationsEnabled
        )
    }
}