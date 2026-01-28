/**
 * Data model for notes
 *
 * @author Muhammad Ali
 * @date 2026-01-28
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.modal

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Represents a note with title and content.
 *
 * @property timestampMillis The note's creation time in milliseconds since epoch (used as primary key)
 * @property title The title of the note
 * @property content The content/body of the note
 */
data class NoteData(
    val timestampMillis: Long,
    val title: String,
    val content: String
) {
    /**
     * Computed formatted date string for display.
     * Converts timestampMillis to a human-readable format like "Jan 28, 2026".
     */
    val formattedDate: String
        get() {
            val instant = Instant.fromEpochMilliseconds(timestampMillis)
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val monthName = monthNames.getOrNull(dateTime.monthNumber - 1) ?: dateTime.monthNumber.toString()
            return "$monthName ${dateTime.dayOfMonth}, ${dateTime.year}"
        }
}
