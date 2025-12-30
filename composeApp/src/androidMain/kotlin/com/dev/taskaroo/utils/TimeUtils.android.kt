/**
 * Android-specific time utility implementations.
 *
 * Provides platform-specific implementations of time-related functions
 * using Android's System.currentTimeMillis() for optimal performance.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Android implementation of currentTimeMillis using System.currentTimeMillis().
 *
 * @return Current timestamp in milliseconds since Unix epoch
 */
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

/**
 * Android implementation of todayDate.
 * Converts current time to LocalDate in the system's timezone.
 *
 * @return Today's date as LocalDate
 */
@OptIn(ExperimentalTime::class)
actual fun todayDate(): LocalDate {
    val currentMillis = currentTimeMillis()
    val instant = Instant.fromEpochMilliseconds(currentMillis)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}
