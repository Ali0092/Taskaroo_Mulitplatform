/**
 * Date and time manipulation utilities for the Taskaroo application.
 *
 * Provides utility functions for converting between LocalDate/LocalDateTime
 * and timestamp milliseconds, calculating day boundaries, and formatting
 * date/time values using the kotlinx-datetime library.
 *
 * All functions use the system's current timezone for conversions.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Utility object for date and time operations.
 * Functions are well-documented below with their specific purposes.
 */
@OptIn(ExperimentalTime::class)
object DateTimeUtils {
    /**
     * Converts LocalDate and time components to milliseconds timestamp
     */
    fun dateTimeToMillis(
        date: LocalDate,
        hour: Int,
        minute: Int
    ): Long {
        val localDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            hour, minute, 0, 0
        )
        return localDateTime.toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    }

    /**
     * Gets start of day in milliseconds for a given LocalDate (00:00:00)
     */
    fun getStartOfDayMillis(date: LocalDate): Long {
        return dateTimeToMillis(date, 0, 0)
    }

    /**
     * Gets end of day in milliseconds for a given LocalDate (23:59:59.999)
     */
    fun getEndOfDayMillis(date: LocalDate): Long {
        return dateTimeToMillis(date, 23, 59) + 59_999  // Add 59 seconds + 999 millis
    }

    /**
     * Converts milliseconds timestamp to LocalDateTime
     */
    fun millisToLocalDateTime(millis: Long): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(millis)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    /**
     * Extracts hour from timestamp (0-23)
     */
    fun getHourFromMillis(millis: Long): Int {
        return millisToLocalDateTime(millis).hour
    }

    /**
     * Gets the hour slot index (0-23) for displaying in hour list
     */
    fun getHourSlotIndex(millis: Long): Int {
        return getHourFromMillis(millis)
    }

    /**
     * Formats time for display (e.g., "3:30 PM")
     */
    fun formatTime(millis: Long): String {
        val dateTime = millisToLocalDateTime(millis)
        val hour = dateTime.hour
        val minute = dateTime.minute
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when (hour) {
            0 -> 12
            in 1..12 -> hour
            else -> hour - 12
        }
        return "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
    }

    /**
     * Formats date for display (e.g., "Dec 27, 2024")
     */
    fun formatDate(date: LocalDate): String {
        val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        return "${monthName.take(3)} ${date.dayOfMonth}, ${date.year}"
    }

    /**
     * Formats full date and time for display (e.g., "2024-12-27 3:30 PM")
     */
    fun formatDateTime(millis: Long): String {
        val dateTime = millisToLocalDateTime(millis)
        return "${dateTime.date} ${formatTime(millis)}"
    }

    /**
     * Checks if a task is overdue based on its deadline timestamp.
     * A task is considered overdue if its timestamp is in the past.
     *
     * @param taskTimestampMillis The task's deadline in milliseconds
     * @return true if the task deadline has passed, false otherwise
     */
    fun isTaskOverdue(taskTimestampMillis: Long): Boolean {
        val currentTimeMillis = currentTimeMillis()
        return taskTimestampMillis < currentTimeMillis
    }
}
