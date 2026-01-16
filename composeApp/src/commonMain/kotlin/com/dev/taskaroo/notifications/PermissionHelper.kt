/**
 * Cross-platform permission request helper for notifications.
 * Provides a Composable function to request notification permissions with proper UI handling.
 *
 * @author Claude Code
 * @date 2026-01-16
 */

package com.dev.taskaroo.notifications

import androidx.compose.runtime.Composable

/**
 * Remember a permission requester for notification permissions.
 * On Android, this uses ActivityResultLauncher.
 * On iOS, this directly requests authorization.
 *
 * @param onPermissionResult Callback with the result of the permission request
 * @return A lambda function that can be called to trigger the permission request
 */
@Composable
expect fun rememberNotificationPermissionRequester(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit
