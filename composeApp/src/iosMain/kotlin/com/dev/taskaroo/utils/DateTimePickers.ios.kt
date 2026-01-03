/**
 * iOS implementation of native date and time picker dialogs
 *
 * @author Muhammad Ali
 * @date 2025-01-04
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeDatePicker(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            val alertController = UIAlertController.alertControllerWithTitle(
                title = "Select Date",
                message = "\n\n\n\n\n\n\n\n",  // Space for date picker
                preferredStyle = UIAlertControllerStyleAlert
            )

            // Style alert controller view
            alertController.view.tintColor = primaryVariantColor

            // Create UIDatePicker
            val datePicker = UIDatePicker().apply {
                setDatePickerMode(UIDatePickerMode.UIDatePickerModeDate)
                setPreferredDatePickerStyle(UIDatePickerStyle.UIDatePickerStyleWheels)

                // Apply brand color tint
                setTintColor(primaryColor)
                setValue(primaryColor, forKey = "textColor")

                // Set initial date
                val components = NSDateComponents().apply {
                    year = initialYear.toLong()
                    month = initialMonth.toLong()
                    day = initialDay.toLong()
                }
                val calendar = NSCalendar.currentCalendar
                val initialDate = calendar.dateFromComponents(components)
                initialDate?.let { setDate(it) }

                // Frame positioning
                setFrame(platform.CoreGraphics.CGRectMake(0.0, 50.0, 270.0, 150.0))
            }

            alertController.view.addSubview(datePicker)

            // Done button with custom color
            val doneAction = UIAlertAction.actionWithTitle(
                title = "Done",
                style = UIAlertActionStyleDefault,
                handler = { _ ->
                    val selectedDate = datePicker.date
                    val calendar = NSCalendar.currentCalendar
                    val components = calendar.components(
                        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
                        fromDate = selectedDate
                    )
                    onDateSelected(
                        components.year.toInt(),
                        components.month.toInt(),
                        components.day.toInt()
                    )
                }
            )
            doneAction.setValue(primaryVariantColor, forKey = "titleTextColor")
            alertController.addAction(doneAction)

            // Cancel button
            val cancelAction = UIAlertAction.actionWithTitle(
                title = "Cancel",
                style = UIAlertActionStyleCancel,
                handler = { _ -> onDismiss() }
            )
            alertController.addAction(cancelAction)

            // Present the alert
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(
                alertController,
                animated = true,
                completion = null
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeTimePicker(
    initialHour: Int,
    initialMinute: Int,
    initialAmPm: String,
    onTimeSelected: (hour: Int, minute: Int, amPm: String) -> Unit,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            val alertController = UIAlertController.alertControllerWithTitle(
                title = "Select Time",
                message = "\n\n\n\n\n\n\n\n",
                preferredStyle = UIAlertControllerStyleAlert
            )

            // Style alert controller
            alertController.view.tintColor = primaryVariantColor

            // Create UIDatePicker for time
            val timePicker = UIDatePicker().apply {
                setDatePickerMode(UIDatePickerMode.UIDatePickerModeTime)
                setPreferredDatePickerStyle(UIDatePickerStyle.UIDatePickerStyleWheels)

                // Apply brand color tint
                setTintColor(primaryColor)
                setValue(primaryColor, forKey = "textColor")

                // Set initial time (convert 12-hour to 24-hour)
                val hour24 = if (initialAmPm == "AM") {
                    if (initialHour == 12) 0 else initialHour
                } else {
                    if (initialHour == 12) 12 else initialHour + 12
                }

                val components = NSDateComponents().apply {
                    hour = hour24.toLong()
                    minute = initialMinute.toLong()
                }
                val calendar = NSCalendar.currentCalendar
                val initialTime = calendar.dateFromComponents(components)
                initialTime?.let { setDate(it) }

                setFrame(platform.CoreGraphics.CGRectMake(0.0, 50.0, 270.0, 150.0))
            }

            alertController.view.addSubview(timePicker)

            // Done button with custom color
            val doneAction = UIAlertAction.actionWithTitle(
                title = "Done",
                style = UIAlertActionStyleDefault,
                handler = { _ ->
                    val selectedTime = timePicker.date
                    val calendar = NSCalendar.currentCalendar
                    val components = calendar.components(
                        NSCalendarUnitHour or NSCalendarUnitMinute,
                        fromDate = selectedTime
                    )

                    // Convert back to 12-hour format
                    val hour24 = components.hour.toInt()
                    val (hour12, amPm) = convertTo12Hour(hour24)

                    onTimeSelected(hour12, components.minute.toInt(), amPm)
                }
            )
            doneAction.setValue(primaryVariantColor, forKey = "titleTextColor")
            alertController.addAction(doneAction)

            // Cancel button
            val cancelAction = UIAlertAction.actionWithTitle(
                title = "Cancel",
                style = UIAlertActionStyleCancel,
                handler = { _ -> onDismiss() }
            )
            alertController.addAction(cancelAction)

            // Present the alert
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(
                alertController,
                animated = true,
                completion = null
            )
        }
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
 * Create UIColor from RGB values (0-255 range)
 */
private fun createUIColor(red: Double, green: Double, blue: Double): UIColor {
    return UIColor(
        red = red / 255.0,
        green = green / 255.0,
        blue = blue / 255.0,
        alpha = 1.0
    )
}

// App brand colors
private val primaryColor = createUIColor(107.0, 128.0, 107.0)        // #6B806B
private val primaryVariantColor = createUIColor(79.0, 99.0, 79.0)    // #4F634F
