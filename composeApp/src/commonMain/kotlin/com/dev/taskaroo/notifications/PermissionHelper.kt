/**
 * Cross-platform permission request helper for notifications.
 * Provides a Composable function to request notification permissions with proper UI handling.
 *
 * @author Claude Code
 * @date 2026-01-16
 */

package com.dev.taskaroo.notifications

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionRequester(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit
