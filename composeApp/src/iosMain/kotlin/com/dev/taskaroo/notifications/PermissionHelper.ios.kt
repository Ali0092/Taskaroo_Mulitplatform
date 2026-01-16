/**
 * iOS implementation of permission request helper.
 * Uses UNUserNotificationCenter to request notification authorization.
 *
 * @author Claude Code
 * @date 2026-01-16
 */

package com.dev.taskaroo.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

/**
 * iOS implementation of notification permission requester.
 * Uses the NotificationScheduler to request authorization from the user.
 */
@Composable
actual fun rememberNotificationPermissionRequester(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit {
    val notificationScheduler = rememberNotificationScheduler()
    val coroutineScope = rememberCoroutineScope()

    return remember {
        {
            coroutineScope.launch {
                try {
                    println("PermissionHelper (iOS): Requesting notification permission")
                    val isGranted = notificationScheduler.requestPermission()
                    println("PermissionHelper (iOS): Permission result - granted = $isGranted")
                    onPermissionResult(isGranted)
                } catch (e: Exception) {
                    println("PermissionHelper (iOS): Error requesting permission - ${e.message}")
                    onPermissionResult(false)
                }
            }
        }
    }
}
