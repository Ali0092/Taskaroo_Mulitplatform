/**
 * Cross-platform notification scheduling interface.
 * Provides platform-agnostic API for scheduling and managing meeting notifications.
 *
 * @author Claude Code
 * @date 2026-01-13
 */

package com.dev.taskaroo.notifications

import androidx.compose.runtime.Composable
import com.dev.taskaroo.modal.TaskData

/**
 * Platform-agnostic interface for scheduling meeting notifications.
 * Implementations handle platform-specific notification APIs (AlarmManager on Android, UNUserNotificationCenter on iOS).
 */
interface NotificationScheduler {
    /**
     * Schedule a notification 15 minutes before the task's due time.
     * Notifications scheduled in the past are silently ignored.
     *
     * @param task The task with meeting details and timestamp
     */
    suspend fun scheduleMeetingNotification(task: TaskData)

    /**
     * Cancel a previously scheduled notification for a task.
     * Safe to call if no notification exists for the timestamp.
     *
     * @param taskTimestamp The task's timestamp (used as notification ID)
     */
    suspend fun cancelNotification(taskTimestamp: Long)

    /**
     * Check if notification permission is currently granted.
     * Does not show any permission dialog - only checks current status.
     *
     * @return true if permission is granted or not needed, false if denied
     */
    suspend fun checkPermissionStatus(): Boolean

    /**
     * Request user permission for sending notifications.
     * On Android 13+, requests POST_NOTIFICATIONS permission.
     * On iOS, requests user notification authorization.
     * On older Android versions, returns true (no permission needed).
     * Should only be called if checkPermissionStatus() returns false.
     *
     * @return true if permission is granted or not needed, false if denied
     */
    suspend fun requestPermission(): Boolean

    /**
     * Cancel all scheduled notifications.
     * Useful for cleanup when the app is uninstalled or user disables notifications.
     */
    suspend fun cancelAllNotifications()
}

/**
 * Composable function that provides a platform-specific NotificationScheduler instance.
 * Uses the expect/actual pattern to return the appropriate implementation.
 *
 * @return A NotificationScheduler instance for the current platform
 */
@Composable
expect fun rememberNotificationScheduler(): NotificationScheduler
