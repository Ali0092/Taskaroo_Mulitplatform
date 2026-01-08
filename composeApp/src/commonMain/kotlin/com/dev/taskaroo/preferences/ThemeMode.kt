package com.dev.taskaroo.preferences

enum class ThemeMode(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark")
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.LIGHT
)