/**
 * iOS implementation of notification scheduling using UNUserNotificationCenter.
 * Schedules local notifications for meeting reminders 15 minutes before due time.
 *
 * @author Claude Code
 * @date 2026-01-13
 */

package com.dev.taskaroo.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.dev.taskaroo.modal.TaskData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSTimeZone
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * iOS implementation of NotificationScheduler.
 * Uses UNUserNotificationCenter to schedule local notifications.
 * Notifications are delivered 15 minutes before the task's due time.
 */
@OptIn(ExperimentalForeignApi::class)
class IOSNotificationScheduler : NotificationScheduler {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    companion object {
        private const val MINUTES_BEFORE = 15
    }

    /**
     * Schedule a meeting notification to fire 15 minutes before the task's due time.
     * If the notification time is in the past, it's silently ignored.
     */
    override suspend fun scheduleMeetingNotification(task: TaskData) {
        val notificationTime = task.timestampMillis - (MINUTES_BEFORE * 60 * 1000)
        val currentTime = NSDate().timeIntervalSince1970 * 1000  // Current time in milliseconds

        if (notificationTime <= currentTime.toLong()) {
            println("IOSNotificationScheduler: Notification time is in the past, skipping")
            return
        }

        dispatch_async(dispatch_get_main_queue()) {
            try {
                // Create notification content with title and message
                val bodyText = if (task.meetingLink.isNotEmpty()) {
                    "${task.subtitle}\n\nMeeting Link: ${task.meetingLink}"
                } else {
                    task.subtitle
                }

                val content = UNMutableNotificationContent().apply {
                    setTitle("Meeting: ${task.title} starts in 15 minutes")
                    setBody(bodyText)
                    setSound(UNNotificationSound.defaultSound())

                    // Add taskTimestamp to userInfo for deep linking when user taps notification
                    setUserInfo(mapOf(
                        "taskTimestamp" to task.timestampMillis.toString(),
                        "meetingLink" to task.meetingLink
                    ))
                }

                // Convert timestamp to date components for calendar trigger
                // NSDate uses timeIntervalSinceReferenceDate (not timeIntervalSince1970)
                // Reference date: January 1, 2001 00:00:00 UTC
                val referenceDate = NSDate(timeIntervalSinceReferenceDate = 0.0)  // 2001-01-01
                val seconds1970To2001 = 978307200.0  // Seconds between 1970 and 2001
                val triggerDate = NSDate(timeIntervalSinceReferenceDate = (notificationTime.toDouble() / 1000.0) - seconds1970To2001)
                val calendar = NSCalendar.currentCalendar

                // Extract date components (year, month, day, hour, minute, second)
                val components = calendar.components(
                    ((1L shl 0) or  // NSCalendarUnitEra
                    (1L shl 1) or  // NSCalendarUnitYear
                    (1L shl 2) or  // NSCalendarUnitMonth
                    (1L shl 3) or  // NSCalendarUnitDay
                    (1L shl 4) or  // NSCalendarUnitHour
                    (1L shl 5) or  // NSCalendarUnitMinute
                    (1L shl 6)).toULong(),    // NSCalendarUnitSecond
                    fromDate = triggerDate
                )

                // Create trigger that will fire at the specified date/time
                val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                    components,
                    repeats = false
                )

                // Create notification request with unique identifier based on task timestamp
                val identifier = "meeting_${task.timestampMillis}"
                val request = UNNotificationRequest.requestWithIdentifier(
                    identifier = identifier,
                    content = content,
                    trigger = trigger
                )

                // Schedule the notification
                notificationCenter.addNotificationRequest(request) { error ->
                    if (error != null) {
                        println("IOSNotificationScheduler: Error scheduling notification - $error")
                    } else {
                        println("IOSNotificationScheduler: Scheduled notification for '${task.title}' at $notificationTime")
                    }
                }
            } catch (e: Exception) {
                println("IOSNotificationScheduler: Exception scheduling notification - ${e.message}")
            }
        }
    }

    /**
     * Cancel a previously scheduled notification.
     * Uses the task timestamp to identify and cancel the correct notification.
     */
    override suspend fun cancelNotification(taskTimestamp: Long) {
        dispatch_async(dispatch_get_main_queue()) {
            try {
                val identifier = "meeting_$taskTimestamp"
                notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(identifier))
                println("IOSNotificationScheduler: Cancelled notification for $taskTimestamp")
            } catch (e: Exception) {
                println("IOSNotificationScheduler: Error cancelling notification - ${e.message}")
            }
        }
    }

    /**
     * Request user notification authorization on iOS.
     * Prompts the user to allow notifications with alert, sound, and badge options.
     * User can change this later in Settings > App > Notifications.
     * This is a blocking call that requests permission synchronously.
     */
    override suspend fun requestPermission(): Boolean {
        // iOS permission requests are asynchronous
        // Request authorization with alert, sound, and badge
        var granted = false
        var completed = false

        dispatch_async(dispatch_get_main_queue()) {
            try {
                notificationCenter.requestAuthorizationWithOptions(
                    UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
                ) { success, error ->
                    granted = success
                    completed = true
                    if (error != null) {
                        println("IOSNotificationScheduler: Permission request error - $error")
                    } else {
                        println("IOSNotificationScheduler: Notification permission granted = $success")
                    }
                }
            } catch (e: Exception) {
                println("IOSNotificationScheduler: Exception requesting permission - ${e.message}")
                granted = true  // Allow to continue even if permission request fails
                completed = true
            }
        }

        // Wait for permission result (with timeout of 3 seconds)
        var waitCount = 0
        while (!completed && waitCount < 30) {
            delay(100)
            waitCount++
        }

        if (!completed) {
            println("IOSNotificationScheduler: Permission request timeout, assuming granted")
            granted = true
        }

        return granted
    }

    /**
     * Cancel all pending notifications.
     * Useful for cleanup when user disables notifications or uninstalls app.
     */
    override suspend fun cancelAllNotifications() {
        dispatch_async(dispatch_get_main_queue()) {
            try {
                notificationCenter.removeAllPendingNotificationRequests()
                println("IOSNotificationScheduler: Cancelled all pending notifications")
            } catch (e: Exception) {
                println("IOSNotificationScheduler: Error cancelling all notifications - ${e.message}")
            }
        }
    }
}

/**
 * Composable provider for NotificationScheduler on iOS.
 * Returns an IOSNotificationScheduler instance.
 */
@Composable
actual fun rememberNotificationScheduler(): NotificationScheduler {
    return remember { IOSNotificationScheduler() }
}
