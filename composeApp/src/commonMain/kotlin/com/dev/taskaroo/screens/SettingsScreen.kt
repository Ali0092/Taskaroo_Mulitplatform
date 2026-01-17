/**
 * Settings screen for managing app preferences.
 * Allows users to toggle theme mode and notification preferences.
 *
 * @author Claude Code
 * @date 2026-01-17
 */
package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.common.TaskarooTopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.notifications.rememberNotificationScheduler
import com.dev.taskaroo.preferences.AppSettings
import com.dev.taskaroo.preferences.ThemeMode
import com.dev.taskaroo.preferences.getPreferencesManager
import com.dev.taskaroo.primaryColorVariant
import kotlinx.coroutines.launch

class SettingsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val preferencesManager = remember { getPreferencesManager() }
        val settings by preferencesManager.settingsFlow.collectAsState(AppSettings())
        val coroutineScope = rememberCoroutineScope()
        val notificationScheduler = rememberNotificationScheduler()
        val databaseHelper = LocalDatabase.current

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top App Bar
                TaskarooTopAppBar(
                    title = "Settings",
                    canShowNavigationIcon = true,
                    onBackButtonClick = {
                        navigator.pop()
                    }
                )

                // Theme Toggle Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            coroutineScope.launch {
                                val newTheme = if (settings.themeMode == ThemeMode.LIGHT) {
                                    ThemeMode.DARK
                                } else {
                                    ThemeMode.LIGHT
                                }
                                preferencesManager.updateThemeMode(newTheme)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Theme",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Current: ${settings.themeMode.displayName}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }

                        // Theme emoji indicator
                        Text(
                            text = if (settings.themeMode == ThemeMode.LIGHT) "🌞" else "🌙",
                            fontSize = 32.sp
                        )
                    }
                }

                // Notification Toggle Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Task Notifications",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Enable reminders for tasks (meetings always notify)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Switch(
                            checked = settings.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                coroutineScope.launch {
                                    preferencesManager.updateNotificationsEnabled(enabled)

                                    // If disabled, cancel all non-meeting notifications
                                    if (!enabled) {
                                        val allTasks = databaseHelper.getAllTasks()
                                        allTasks.forEach { task ->
                                            // Only cancel non-meeting, undone tasks
                                            if (!task.isMeeting && !task.isDone) {
                                                notificationScheduler.cancelNotification(task.timestampMillis)
                                            }
                                        }
                                        println("SettingsScreen: Cancelled all non-meeting notifications")
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.background,
                                checkedTrackColor = primaryColorVariant,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                // Information text about meetings
                Text(
                    text = "Note: Meeting tasks will always send notifications 15 minutes before, regardless of this setting.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
