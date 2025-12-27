package com.dev.taskaroo.modal

import com.dev.taskaroo.utils.DateTimeUtils
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

data class TaskItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

data class TaskData(
    val timestampMillis: Long,  // PRIMARY KEY - task date+time in milliseconds
    val id: String = timestampMillis.toString(),
    val title: String,
    val subtitle: String,
    val category: String, // "urgent", "medium", "high", "low"
    val taskList: List<TaskItem>,
    var completedTasks: Int = 0
) {
    // Compute deadline string from timestampMillis for display
    @OptIn(ExperimentalTime::class)
    val deadline: String
        get() {
            val instant = Instant.fromEpochMilliseconds(timestampMillis)
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            return "${dateTime.date} ${dateTime.time.hour}:${dateTime.time.minute.toString().padStart(2, '0')}"
        }

    val progress: Float
        get() = if (taskList.isEmpty()) 0f else completedTasks.toFloat() / taskList.size

    val progressPercentage: Int
        get() = (progress * 100).toInt()

    val progressText: String
        get() = "$progressPercentage% ($completedTasks/${taskList.size} tasks)"
}