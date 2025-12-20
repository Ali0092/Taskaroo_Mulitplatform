package com.dev.taskaroo.modal

data class TaskItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

data class TaskData(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // "urgent", "medium", "high", "low"
    val deadline: String,
    val taskList: List<TaskItem>,
    var completedTasks: Int = 0
) {
    val progress: Float
        get() = if (taskList.isEmpty()) 0f else completedTasks.toFloat() / taskList.size

    val progressPercentage: Int
        get() = (progress * 100).toInt()

    val progressText: String
        get() = "$progressPercentage% ($completedTasks/${taskList.size} tasks)"
}