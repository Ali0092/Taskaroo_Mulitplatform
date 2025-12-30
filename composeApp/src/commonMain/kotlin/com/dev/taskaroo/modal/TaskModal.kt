/**
 * Data models for task management
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.modal

import com.dev.taskaroo.utils.DateTimeUtils
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Represents an individual task item within a task group.
 *
 * @property id Unique identifier for the task item
 * @property text The task description or content
 * @property isCompleted Indicates whether the task has been completed (default: false)
 */
data class TaskItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

/**
 * Represents a complete task with multiple sub-tasks and metadata.
 * This is the main data model for managing tasks in the application.
 *
 * @property timestampMillis The task's date and time in milliseconds since epoch (used as primary key)
 * @property id Unique identifier for the task, defaults to timestampMillis as a string
 * @property title The main title of the task
 * @property subtitle Additional description or subtitle for the task
 * @property category The priority category of the task: "urgent", "medium", "high", or "low"
 * @property taskList List of individual task items belonging to this task
 * @property completedTasks Number of completed tasks in the taskList
 */
data class TaskData(
    val timestampMillis: Long,  // PRIMARY KEY - task date+time in milliseconds
    val id: String = timestampMillis.toString(),
    val title: String,
    val subtitle: String,
    val category: String, // "urgent", "medium", "high", "low"
    val taskList: List<TaskItem>,
    var completedTasks: Int = 0
) {
    /**
     * Computed deadline string formatted for display.
     * Converts timestampMillis to a human-readable date and time format.
     *
     * @return Formatted string in the format "YYYY-MM-DD HH:MM"
     */
    @OptIn(ExperimentalTime::class)
    val deadline: String
        get() {
            val instant = Instant.fromEpochMilliseconds(timestampMillis)
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            return "${dateTime.date} ${dateTime.time.hour}:${dateTime.time.minute.toString().padStart(2, '0')}"
        }

    /**
     * Calculates the completion progress as a fraction between 0 and 1.
     *
     * @return Progress value (0.0 to 1.0), or 0 if taskList is empty
     */
    val progress: Float
        get() = if (taskList.isEmpty()) 0f else completedTasks.toFloat() / taskList.size

    /**
     * Calculates the completion progress as a percentage.
     *
     * @return Progress as an integer percentage (0 to 100)
     */
    val progressPercentage: Int
        get() = (progress * 100).toInt()

    /**
     * Generates a formatted progress text displaying percentage and task counts.
     *
     * @return Formatted string like "75% (3/4 tasks)"
     */
    val progressText: String
        get() = "$progressPercentage% ($completedTasks/${taskList.size} tasks)"
}