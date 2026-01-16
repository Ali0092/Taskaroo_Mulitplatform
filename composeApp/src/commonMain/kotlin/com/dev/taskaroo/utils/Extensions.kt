package com.dev.taskaroo.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime


// Date and time formatting functions
@OptIn(ExperimentalTime::class)
fun Long.formatDateDisplay(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val monthName = monthNames.getOrNull(dateTime.monthNumber - 1) ?: dateTime.monthNumber.toString()
    return "$monthName ${dateTime.dayOfMonth}, ${dateTime.year}"
}

@OptIn(ExperimentalTime::class)
fun Long.formatTimeDisplay(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour24 = dateTime.hour
    val minute = dateTime.minute

    // Convert to 12-hour format
    val (hour12, amPm) = when {
        hour24 == 0 -> Pair(12, "AM")
        hour24 < 12 -> Pair(hour24, "AM")
        hour24 == 12 -> Pair(12, "PM")
        else -> Pair(hour24 - 12, "PM")
    }

    return "${hour12.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
}
