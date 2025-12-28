package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.common.CapsuleFloatingActionButton
import com.dev.taskaroo.common.DeleteConfirmationDialog
import com.dev.taskaroo.screens.CreateTaskScreen
import com.dev.taskaroo.common.TaskCard
import com.dev.taskaroo.common.TaskChipRow
import com.dev.taskaroo.common.TopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import com.dev.taskaroo.utils.currentTimeMillis
import kotlinx.coroutines.launch
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.menu_icon

class MainScreen : Screen {

    @Composable
    override fun Content() {
        // Sample task data with all priority levels

        val titles = listOf(
            "Make Every Day Count Forward ✨",
            "Small Tasks, Big Wins Daily 🚀",
            "Plan Less, Achieve More Today 📅",
            "Your Daily Progress Starts Here 🌱",
            "Turn Goals Into Daily Action 🔥",
            "Focus Today, Grow Tomorrow Strong 💪",
            "Build Momentum One Task Daily ⚡",
            "Clear Mind, Complete More Today 🧠"
        )

        // State for selected category
        var selectedCategory by remember { mutableStateOf("Work") }
        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()
        var tasks by remember { mutableStateOf<List<TaskData>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        // Delete dialog state
        var showDeleteDialog by remember { mutableStateOf(false) }
        var taskToDelete by remember { mutableStateOf<TaskData?>(null) }

        // Load tasks when screen appears
        LaunchedEffect(Unit) {
            try {
                val allTasks = databaseHelper.getAllTasks()
                val currentTime = currentTimeMillis()

                // Filter: only upcoming tasks (timestampMillis >= current time)
                // Sort: by deadline ascending (closest first)
                tasks = allTasks
                    .filter { it.timestampMillis >= currentTime }
                    .sortedBy { it.timestampMillis }

                println("MainScreen: Loaded ${tasks.size} upcoming tasks from ${allTasks.size} total")
            } catch (e: Exception) {
                println("MainScreen: Error loading tasks - ${e.message}")
                e.printStackTrace()
                tasks = emptyList()
            } finally {
                isLoading = false
            }
        }

        Scaffold(
            floatingActionButton = {
                // Floating Action Button
                CapsuleFloatingActionButton(
                    onAddClick = {
                        navigator.push(CalendarScreen())
                    },
                )
            }
        ) { innerPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(innerPaddings)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {

                TopAppBar(
                    title = titles.random(),
                    canShowNavigationIcon = false,
                    otherIcon = Res.drawable.menu_icon,
                    onOtherIconClick = {
                        // later this will be added...
                    }
                )

                Spacer(Modifier.height(16.dp))

//                TaskChipRow(
//                    onCategorySelected = { category ->
//                        selectedCategory = category
//                    }
//                )

                // Filter tasks based on selected category
//                val filteredTasks = when (selectedCategory) {
//                    "Work" -> sampleTasks.filter { it.category in listOf("High", "Medium") }
//                    "Personal" -> sampleTasks.filter { it.category == "Low" }
//                    "Shopping" -> sampleTasks.filter { it.category == "Medium" }
//                    "Health" -> sampleTasks.filter { it.category in listOf("Urgent", "High") }
//                    else -> sampleTasks
//                }

                // LazyColumn with task cards
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Loading tasks...",
                                    color = onBackgroundColor.copy(alpha = 0.5f)
                                )
                            }
                        }
                    } else if (tasks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No upcoming tasks!\nTap the + button to create one.",
                                    color = onBackgroundColor.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(tasks, key = { it.timestampMillis }) { task ->
                            TaskCard(
                                taskData = task,
                                onTaskItemToggle = { taskItemId, isChecked ->
                                    // Update task item completion in database
                                    coroutineScope.launch {
                                        try {
                                            databaseHelper.toggleTaskItemCompletion(taskItemId, isChecked)

                                            // Update local state
                                            tasks = tasks.map { currentTask ->
                                                if (currentTask.timestampMillis == task.timestampMillis) {
                                                    val updatedTaskList = currentTask.taskList.map { item ->
                                                        if (item.id == taskItemId) {
                                                            item.copy(isCompleted = isChecked)
                                                        } else {
                                                            item
                                                        }
                                                    }
                                                    val completedCount = updatedTaskList.count { it.isCompleted }

                                                    // Update completed count in DB
                                                    databaseHelper.updateCompletedCount(
                                                        currentTask.timestampMillis,
                                                        completedCount
                                                    )

                                                    currentTask.copy(
                                                        taskList = updatedTaskList,
                                                        completedTasks = completedCount
                                                    )
                                                } else {
                                                    currentTask
                                                }
                                            }
                                        } catch (e: Exception) {
                                            println("MainScreen: Error toggling task item - ${e.message}")
                                        }
                                    }
                                },
                                onClick = {
                                    navigator.push(CreateTaskScreen(taskTimestampToEdit = task.timestampMillis))
                                },
                                onLongClick = {
                                    taskToDelete = task
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // Delete confirmation dialog
            if (showDeleteDialog && taskToDelete != null) {
                DeleteConfirmationDialog(
                    showDialog = showDeleteDialog,
                    taskTitle = taskToDelete?.title ?: "",
                    onDismiss = {
                        showDeleteDialog = false
                        taskToDelete = null
                    },
                    onConfirm = {
                        coroutineScope.launch {
                            try {
                                taskToDelete?.let { task ->
                                    databaseHelper.deleteTask(task.timestampMillis)

                                    // Remove from local state immediately
                                    tasks = tasks.filter { it.timestampMillis != task.timestampMillis }

                                    showDeleteDialog = false
                                    taskToDelete = null
                                }
                            } catch (e: Exception) {
                                println("MainScreen: Error deleting task - ${e.message}")
                                showDeleteDialog = false
                                taskToDelete = null
                            }
                        }
                    }
                )
            }
        }
    }

}