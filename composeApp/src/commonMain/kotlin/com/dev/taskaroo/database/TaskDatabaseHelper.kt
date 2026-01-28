/**
 * Database operations layer for task management using SQLDelight.
 *
 * Provides a high-level API for CRUD operations on tasks and task items,
 * including transaction management, Flow-based observables for real-time updates,
 * and coroutine-based asynchronous database access.
 *
 * All database operations are executed on the Default dispatcher for optimal
 * performance and to avoid blocking the main thread.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import com.dev.taskaroo.modal.NoteData
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TaskDatabaseHelper(sqlDriver: SqlDriver) {
    private val database = TaskDatabase(sqlDriver)
    private val taskQueries = database.taskQueries

    /**
     * Insert a new task with its task items into the database
     */
    suspend fun insertTask(taskData: TaskData) = withContext(Dispatchers.Default) {
        database.transaction {
            // Insert main task
            taskQueries.insertTask(
                timestampMillis = taskData.timestampMillis,
                title = taskData.title,
                subtitle = taskData.subtitle,
                completedTasksCount = taskData.completedTasks.toLong(),
                isTaskDone = if (taskData.isDone) 1L else 0L,
                isMeeting = if (taskData.isMeeting) 1L else 0L,
                meetingLink = taskData.meetingLink
            )

            // Insert task items
            taskData.taskList.forEach { taskItem ->
                taskQueries.insertTaskItem(
                    id = taskItem.id,
                    taskTimestamp = taskData.timestampMillis,
                    text = taskItem.text,
                    isCompleted = if (taskItem.isCompleted) 1L else 0L
                )
            }
        }
    }

    /**
     * Get all tasks from the database
     */
    suspend fun getAllTasks(): List<TaskData> = withContext(Dispatchers.Default) {
        val tasks = taskQueries.getAllTasks().executeAsList()
        tasks.map { task ->
            val taskItems = taskQueries.getTaskItemsForTask(task.timestampMillis).executeAsList()
            TaskData(
                timestampMillis = task.timestampMillis,
                title = task.title,
                subtitle = task.subtitle,
                taskList = taskItems.map { item ->
                    TaskItem(
                        id = item.id,
                        text = item.text,
                        isCompleted = item.isCompleted == 1L
                    )
                },
                completedTasks = task.completedTasksCount.toInt(),
                isDone = task.isTaskDone == 1L,
                isMeeting = task.isMeeting == 1L,
                meetingLink = task.meetingLink
            )
        }
    }

    /**
     * Get tasks for a specific date range (startMillis to endMillis)
     */
    suspend fun getTasksForDate(startMillis: Long, endMillis: Long): List<TaskData> =
        withContext(Dispatchers.Default) {
            val tasks = taskQueries.getTasksForDate(startMillis, endMillis).executeAsList()
            tasks.map { task ->
                val taskItems = taskQueries.getTaskItemsForTask(task.timestampMillis).executeAsList()
                TaskData(
                    timestampMillis = task.timestampMillis,
                    title = task.title,
                    subtitle = task.subtitle,
                    taskList = taskItems.map { item ->
                        TaskItem(
                            id = item.id,
                            text = item.text,
                            isCompleted = item.isCompleted == 1L
                        )
                    },
                    completedTasks = task.completedTasksCount.toInt(),
                    isDone = task.isTaskDone == 1L,
                    isMeeting = task.isMeeting == 1L,
                    meetingLink = task.meetingLink
                )
            }
        }

    /**
     * Get a specific task by timestamp
     */
    suspend fun getTaskByTimestamp(timestamp: Long): TaskData? = withContext(Dispatchers.Default) {
        val task = taskQueries.getTaskByTimestamp(timestamp).executeAsOneOrNull() ?: return@withContext null
        val taskItems = taskQueries.getTaskItemsForTask(task.timestampMillis).executeAsList()
        TaskData(
            timestampMillis = task.timestampMillis,
            title = task.title,
            subtitle = task.subtitle,
            taskList = taskItems.map { item ->
                TaskItem(
                    id = item.id,
                    text = item.text,
                    isCompleted = item.isCompleted == 1L
                )
            },
            completedTasks = task.completedTasksCount.toInt(),
            isDone = task.isTaskDone == 1L,
            isMeeting = task.isMeeting == 1L,
            meetingLink = task.meetingLink
        )
    }

    /**
     * Delete a task and all its task items
     */
    suspend fun deleteTask(timestamp: Long) = withContext(Dispatchers.Default) {
        database.transaction {
            taskQueries.deleteTaskItemsForTask(timestamp)
            taskQueries.deleteTask(timestamp)
        }
    }

    /**
     * Toggle task item completion status
     */
    suspend fun toggleTaskItemCompletion(taskItemId: String, isCompleted: Boolean) =
        withContext(Dispatchers.Default) {
            taskQueries.updateTaskItemCompleted(
                isCompleted = if (isCompleted) 1L else 0L,
                id = taskItemId
            )
        }

    /**
     * Update the completed tasks count for a task
     */
    suspend fun updateCompletedCount(timestamp: Long, count: Int) =
        withContext(Dispatchers.Default) {
            taskQueries.updateCompletedCount(
                completedTasksCount = count.toLong(),
                timestampMillis = timestamp
            )
        }

    /**
     * Update an existing task and its task items
     * Note: timestamp cannot be changed (it's the primary key)
     */
    suspend fun updateTask(taskData: TaskData) = withContext(Dispatchers.Default) {
        database.transaction {
            // Update main task fields
            taskQueries.updateTask(
                title = taskData.title,
                subtitle = taskData.subtitle,
                isTaskDone = if (taskData.isDone) 1L else 0L,
                isMeeting = if (taskData.isMeeting) 1L else 0L,
                meetingLink = taskData.meetingLink,
                timestampMillis = taskData.timestampMillis
            )

            // Delete all existing task items for this task
            taskQueries.deleteTaskItemsForTask(taskData.timestampMillis)

            // Re-insert all task items with their current state
            taskData.taskList.forEach { taskItem ->
                taskQueries.insertTaskItem(
                    id = taskItem.id,
                    taskTimestamp = taskData.timestampMillis,
                    text = taskItem.text,
                    isCompleted = if (taskItem.isCompleted) 1L else 0L
                )
            }

            // Update completed count based on current task items
            val completedCount = taskData.taskList.count { it.isCompleted }
            taskQueries.updateCompletedCount(
                completedTasksCount = completedCount.toLong(),
                timestampMillis = taskData.timestampMillis
            )
        }
    }

    /**
     * Update task done status
     */
    suspend fun updateTaskDoneStatus(timestamp: Long, isDone: Boolean) =
        withContext(Dispatchers.Default) {
            taskQueries.updateTaskDoneStatus(
                isTaskDone = if (isDone) 1L else 0L,
                timestampMillis = timestamp
            )
        }

    /**
     * Get all tasks as a Flow for reactive updates
     */
    fun getAllTasksFlow(): Flow<List<TaskData>> {
        return taskQueries.getAllTasks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { tasks ->
                tasks.map { task ->
                    val taskItems = taskQueries.getTaskItemsForTask(task.timestampMillis).executeAsList()
                    TaskData(
                        timestampMillis = task.timestampMillis,
                        title = task.title,
                        subtitle = task.subtitle,
                        taskList = taskItems.map { item ->
                            TaskItem(
                                id = item.id,
                                text = item.text,
                                isCompleted = item.isCompleted == 1L
                            )
                        },
                        completedTasks = task.completedTasksCount.toInt(),
                        isDone = task.isTaskDone == 1L,
                        isMeeting = task.isMeeting == 1L,
                        meetingLink = task.meetingLink
                    )
                }
            }
    }

    /**
     * Get all meeting tasks from the database
     */
    suspend fun getMeetingTasks(): List<TaskData> = withContext(Dispatchers.Default) {
        val tasks = taskQueries.getMeetingTasks().executeAsList()
        tasks.map { task ->
            val taskItems = taskQueries.getTaskItemsForTask(task.timestampMillis).executeAsList()
            TaskData(
                timestampMillis = task.timestampMillis,
                title = task.title,
                subtitle = task.subtitle,
                taskList = taskItems.map { item ->
                    TaskItem(
                        id = item.id,
                        text = item.text,
                        isCompleted = item.isCompleted == 1L
                    )
                },
                completedTasks = task.completedTasksCount.toInt(),
                isDone = task.isTaskDone == 1L,
                isMeeting = task.isMeeting == 1L,
                meetingLink = task.meetingLink
            )
        }
    }

    // ==========================================================================
    // Note Operations
    // ==========================================================================

    /**
     * Insert a new note into the database
     */
    suspend fun insertNote(noteData: NoteData) = withContext(Dispatchers.Default) {
        taskQueries.insertNote(
            timestampMillis = noteData.timestampMillis,
            title = noteData.title,
            content = noteData.content
        )
    }

    /**
     * Get all notes from the database (newest first)
     */
    suspend fun getAllNotes(): List<NoteData> = withContext(Dispatchers.Default) {
        val notes = taskQueries.getAllNotes().executeAsList()
        notes.map { note ->
            NoteData(
                timestampMillis = note.timestampMillis,
                title = note.title,
                content = note.content
            )
        }
    }

    /**
     * Get a specific note by timestamp
     */
    suspend fun getNoteByTimestamp(timestamp: Long): NoteData? = withContext(Dispatchers.Default) {
        val note = taskQueries.getNoteByTimestamp(timestamp).executeAsOneOrNull()
            ?: return@withContext null
        NoteData(
            timestampMillis = note.timestampMillis,
            title = note.title,
            content = note.content
        )
    }

    /**
     * Update an existing note
     */
    suspend fun updateNote(noteData: NoteData) = withContext(Dispatchers.Default) {
        taskQueries.updateNote(
            title = noteData.title,
            content = noteData.content,
            timestampMillis = noteData.timestampMillis
        )
    }

    /**
     * Delete a note by timestamp
     */
    suspend fun deleteNote(timestamp: Long) = withContext(Dispatchers.Default) {
        taskQueries.deleteNote(timestamp)
    }
}
