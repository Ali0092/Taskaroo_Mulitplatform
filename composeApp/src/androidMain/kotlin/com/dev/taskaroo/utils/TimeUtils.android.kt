package com.dev.taskaroo.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Android implementation of currentTimeMillis
 */
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

/**
 * Android implementation of todayDate
 */
@OptIn(ExperimentalTime::class)
actual fun todayDate(): LocalDate {
    val currentMillis = currentTimeMillis()
    val instant = Instant.fromEpochMilliseconds(currentMillis)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}
