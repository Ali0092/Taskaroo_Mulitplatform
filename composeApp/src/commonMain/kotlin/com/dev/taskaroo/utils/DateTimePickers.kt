/**
 * Platform-specific date and time picker dialogs
 *
 * @author Muhammad Ali
 * @date 2025-01-04
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.utils

import androidx.compose.runtime.Composable

/**
 * Show native date picker dialog
 *
 * @param initialYear Initial year to display
 * @param initialMonth Initial month to display (1-12)
 * @param initialDay Initial day to display (1-31)
 * @param onDateSelected Callback when date is selected (year, month 1-12, day)
 * @param onDismiss Callback when picker is dismissed without selection
 */
@Composable
expect fun NativeDatePicker(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
)

/**
 * Show native time picker dialog in 12-hour format
 *
 * @param initialHour Initial hour to display (1-12)
 * @param initialMinute Initial minute to display (0-59)
 * @param initialAmPm Initial AM/PM ("AM" or "PM")
 * @param onTimeSelected Callback when time is selected (hour 1-12, minute 0-59, amPm)
 * @param onDismiss Callback when picker is dismissed without selection
 */
@Composable
expect fun NativeTimePicker(
    initialHour: Int,
    initialMinute: Int,
    initialAmPm: String,
    onTimeSelected: (hour: Int, minute: Int, amPm: String) -> Unit,
    onDismiss: () -> Unit
)
