/**
 * Android implementation of native date and time picker dialogs
 *
 * @author Muhammad Ali
 * @date 2025-01-04
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.dev.taskaroo.R

@Composable
actual fun NativeDatePicker(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Wrap context with custom theme
        val themedContext = ContextThemeWrapper(context, R.style.AppDatePickerTheme)

        val dialog = DatePickerDialog(
            themedContext,
            { _, year, month, dayOfMonth ->
                // month is 0-based in DatePickerDialog, convert to 1-based
                onDateSelected(year, month + 1, dayOfMonth)
            },
            initialYear,
            initialMonth - 1,  // Convert 1-based to 0-based for DatePickerDialog
            initialDay
        )

        dialog.setOnCancelListener {
            onDismiss()
        }

        dialog.setOnDismissListener {
            onDismiss()
        }

        // Apply button colors programmatically
        dialog.setOnShowListener {
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                ?.setTextColor(hexToColor("#4F634F"))
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                ?.setTextColor(hexToColor("#4F634F"))
        }

        dialog.show()
    }
}

@Composable
actual fun NativeTimePicker(
    initialHour: Int,
    initialMinute: Int,
    initialAmPm: String,
    onTimeSelected: (hour: Int, minute: Int, amPm: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Wrap context with custom theme
        val themedContext = ContextThemeWrapper(context, R.style.AppTimePickerTheme)

        // Convert 12-hour to 24-hour for TimePickerDialog
        val hour24 = if (initialAmPm == "AM") {
            if (initialHour == 12) 0 else initialHour
        } else {
            if (initialHour == 12) 12 else initialHour + 12
        }

        val dialog = TimePickerDialog(
            themedContext,
            { _, hourOfDay, minute ->
                // Convert 24-hour back to 12-hour format
                val (hour12, amPm) = convertTo12Hour(hourOfDay)
                onTimeSelected(hour12, minute, amPm)
            },
            hour24,
            initialMinute,
            false  // Use 12-hour format
        )

        dialog.setOnCancelListener {
            onDismiss()
        }

        dialog.setOnDismissListener {
            onDismiss()
        }

        // Apply button colors programmatically
        dialog.setOnShowListener {
            dialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                ?.setTextColor(hexToColor("#4F634F"))
            dialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                ?.setTextColor(hexToColor("#4F634F"))
        }

        dialog.show()
    }
}

/**
 * Convert 24-hour format to 12-hour format with AM/PM
 */
private fun convertTo12Hour(hour24: Int): Pair<Int, String> {
    return when {
        hour24 == 0 -> Pair(12, "AM")
        hour24 < 12 -> Pair(hour24, "AM")
        hour24 == 12 -> Pair(12, "PM")
        else -> Pair(hour24 - 12, "PM")
    }
}

/**
 * Convert hex color string to Android Color integer
 */
private fun hexToColor(hex: String): Int {
    return android.graphics.Color.parseColor(hex)
}
