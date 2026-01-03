package com.dev.taskaroo.preferences

enum class ThemeMode(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System default")
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)