/**
 * Android implementation of notification scheduling using AlarmManager.
 * Schedules exact alarms for meeting reminders 15 minutes before due time.
 *
 * @author Claude Code
 * @date 2026-01-13
 */

package com.dev.taskaroo.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dev.taskaroo.MainActivity
import com.dev.taskaroo.modal.TaskData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of NotificationScheduler.
 * Uses AlarmManager to schedule exact alarms for meeting reminders.
 * Notifications are delivered 15 minutes before the task's due time.
 */
class AndroidNotificationScheduler(
    private val context: Context
) : NotificationScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "meeting_notifications"
        const val CHANNEL_NAME = "Meeting Reminders"
        private const val MINUTES_BEFORE = 15
    }

    init {
        createNotificationChannel()
    }

    /**
     * Creates the notification channel for Android 8.0+.
     * Sets high importance to ensure notifications are prominent.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming meetings"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedule a meeting notification to fire 15 minutes before the task's due time.
     * If the notification time is in the past, it's silently ignored.
     */
    override suspend fun scheduleMeetingNotification(task: TaskData) =
        withContext(Dispatchers.IO) {
            val notificationTime = task.timestampMillis - (MINUTES_BEFORE * 60 * 1000)
            val currentTime = System.currentTimeMillis()

            if (notificationTime <= currentTime) {
                println("AndroidNotificationScheduler: Notification time is in the past, skipping")
                return@withContext
            }

            // Create intent to broadcast when alarm triggers
            val intent = Intent(context, MeetingNotificationReceiver::class.java).apply {
                putExtra("taskTimestamp", task.timestampMillis)
                putExtra("taskTitle", task.title)
                putExtra("taskSubtitle", task.subtitle)
                putExtra("task_meeting_link", task.meetingLink)
            }

            // Use timestamp as unique request code for PendingIntent
            val requestCode = task.timestampMillis.toInt()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule alarm based on Android version capabilities
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12+: Check if we can schedule exact alarms
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime,
                            pendingIntent
                        )
                    } else {
                        println("AndroidNotificationScheduler: Cannot schedule exact alarms, using inexact alarm instead")
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime,
                            pendingIntent
                        )
                    }
                } else {
                    // Android 11 and below
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime,
                        pendingIntent
                    )
                }
                println("AndroidNotificationScheduler: Scheduled notification for '${task.title}' at $notificationTime")
            } catch (e: Exception) {
                println("AndroidNotificationScheduler: Error scheduling alarm - ${e.message}")
            }
        }

    /**
     * Cancel a previously scheduled notification.
     * Uses the task timestamp to identify and cancel the correct alarm.
     */
    override suspend fun cancelNotification(taskTimestamp: Long) = withContext(Dispatchers.IO) {
        val intent = Intent(context, MeetingNotificationReceiver::class.java)
        val requestCode = taskTimestamp.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            println("AndroidNotificationScheduler: Cancelled notification for $taskTimestamp")
        } catch (e: Exception) {
            println("AndroidNotificationScheduler: Error cancelling alarm - ${e.message}")
        }
    }

    /**
     * Check and request POST_NOTIFICATIONS permission on Android 13+.
     * On older versions, the permission is not required.
     * Note: This only checks the current permission state. Actual permission
     * request UI should be handled by the Activity.
     */
    override suspend fun requestPermission(): Boolean = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                println("AndroidNotificationScheduler: POST_NOTIFICATIONS permission NOT granted")
                println("AndroidNotificationScheduler: User needs to grant notification permission in Settings or via system dialog")
            } else {
                println("AndroidNotificationScheduler: POST_NOTIFICATIONS permission IS granted")
            }
            hasPermission
        } else {
            // Permission not required on older Android versions (pre-Android 13)
            println("AndroidNotificationScheduler: Running on Android < 13, permission not required")
            true
        }
    }

    /**
     * Cancel all scheduled notifications.
     * Not fully implemented - would require tracking all notification IDs.
     */
    override suspend fun cancelAllNotifications() {
        // TODO: Implement if needed for full notification cleanup
        println("AndroidNotificationScheduler: cancelAllNotifications() called")
    }
}

/**
 * BroadcastReceiver that handles notification display when alarm triggers.
 * Called when the scheduled alarm fires and displays the notification to the user.
 * Clicking the notification opens the app to the task details screen.
 * The notification is automatically cancelled after being displayed (fires only once).
 */
class MeetingNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTimestamp = intent.getLongExtra("taskTimestamp", 0L)
        val taskTitle = intent.getStringExtra("taskTitle") ?: "Meeting"
        val taskSubtitle = intent.getStringExtra("taskSubtitle") ?: ""
        val taskMeetingLink = intent.getStringExtra("task_meeting_link") ?: ""

        println("MeetingNotificationReceiver: Received notification for '$taskTitle'")

        // Create intent to open MainActivity with task timestamp
        // This will be handled by MainActivity to navigate to the task details
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("openTaskTimestamp", taskTimestamp)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskTimestamp.toInt(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification with user-friendly message
        val notificationText = if (taskMeetingLink.isNotEmpty()) {
            "$taskSubtitle\n\nMeeting Link: $taskMeetingLink"
        } else {
            taskSubtitle
        }

        val notification = NotificationCompat.Builder(context, AndroidNotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Meeting: $taskTitle starts in 15 minutes")
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show notification
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                NotificationManagerCompat.from(context).notify(taskTimestamp.toInt(), notification)
                println("MeetingNotificationReceiver: Displayed notification for '$taskTitle'")

                // Cancel the notification after displaying it (ensures it only fires once)
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val cancelIntent = Intent(context, MeetingNotificationReceiver::class.java)
                val cancelPendingIntent = PendingIntent.getBroadcast(
                    context,
                    taskTimestamp.toInt(),
                    cancelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(cancelPendingIntent)
                println("MeetingNotificationReceiver: Cancelled recurring alarm for '$taskTitle'")
            } else {
                println("MeetingNotificationReceiver: POST_NOTIFICATIONS permission not granted")
            }
        } catch (e: Exception) {
            println("MeetingNotificationReceiver: Error displaying notification - ${e.message}")
        }
    }
}

/**
 * Composable provider for NotificationScheduler on Android.
 * Returns an AndroidNotificationScheduler instance initialized with the current context.
 */
@Composable
actual fun rememberNotificationScheduler(): NotificationScheduler {
    val context = LocalContext.current
    return remember { AndroidNotificationScheduler(context) }
}
