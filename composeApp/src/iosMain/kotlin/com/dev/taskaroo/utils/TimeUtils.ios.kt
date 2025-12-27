package com.dev.taskaroo.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.time.ExperimentalTime

/**
 * iOS implementation of currentTimeMillis
 */
actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

/**
 * iOS implementation of todayDate
 */
@OptIn(ExperimentalTime::class)
actual fun todayDate(): LocalDate {
    val currentMillis = currentTimeMillis()
    val instant = Instant.fromEpochMilliseconds(currentMillis)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}
