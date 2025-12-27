package com.dev.taskaroo.utils

import kotlinx.datetime.LocalDate

/**
 * Get current time in milliseconds since epoch
 */
expect fun currentTimeMillis(): Long

/**
 * Get today's date in the current system timezone
 */
expect fun todayDate(): LocalDate
