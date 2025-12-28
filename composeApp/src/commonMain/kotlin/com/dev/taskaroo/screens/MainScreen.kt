package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.common.CapsuleFloatingActionButton
import com.dev.taskaroo.common.DeleteConfirmationDialog
import com.dev.taskaroo.common.TaskCard
import com.dev.taskaroo.common.TaskChipRow
import com.dev.taskaroo.common.TopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.utils.currentTimeMillis
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.no_data_placeholder

class MainScreen : Screen {

    @Composable
    override fun Content() {
        // Sample task data with all priority levels

        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()

        // Filter state - persists during navigation session
        var selectedFilter by rememberSaveable { mutableStateOf("Upcoming") }

        // Track all tasks for chip visibility
        var allTasks by remember { mutableStateOf<List<TaskData>>(emptyList()) }
        var tasks by remember { mutableStateOf<List<TaskData>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        // Delete dialog state
        var showDeleteDialog by remember { mutableStateOf(false) }
        var taskToDelete by remember { mutableStateOf<TaskData?>(null) }

        // Load all tasks initially and when screen appears
        LaunchedEffect(Unit) {
            try {
                isLoading = true
                allTasks = databaseHelper.getAllTasks()
            } catch (e: Exception) {
                e.printStackTrace()
                allTasks = emptyList()
            } finally {
                isLoading = false
            }
        }

        // Filter and sort tasks based on selected filter
        LaunchedEffect(selectedFilter, allTasks) {
            val currentTime = currentTimeMillis()

            tasks = when (selectedFilter) {
                "Upcoming" -> {
                    allTasks
                        .filter { it.timestampMillis >= currentTime }
                        .sortedBy { it.timestampMillis }  // Closest deadline first
                }

                "All" -> {
                    allTasks.sortedBy { it.timestampMillis }  // Oldest first
                }

                else -> allTasks.sortedBy { it.timestampMillis }
            }

            println("MainScreen: Filtered to ${tasks.size} tasks for filter '$selectedFilter'")
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
                    title = "Make every day count forward✨",
                    canShowNavigationIcon = false,
                    otherIcon = null,
                    onOtherIconClick = {
                        // later this will be added...
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Show filter chips only when there are tasks
                if (allTasks.isNotEmpty()) {
                    TaskChipRow(
                        categories = listOf("Upcoming", "All"),
                        onCategorySelected = { filter ->
                            selectedFilter = filter
                        }
                    )
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading tasks...",
                            color = onBackgroundColor.copy(alpha = 0.5f)
                        )
                    }
                } else if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.no_data_placeholder),
                                contentDescription = null,
                                tint = onBackgroundColor.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No tasks!\nTap the button at\nbottom right to create one.",
                                color = onBackgroundColor.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // LazyColumn with task cards
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        items(tasks, key = { it.timestampMillis }) { task ->
                            TaskCard(
                                taskData = task,
                                onTaskItemToggle = { taskItemId, isChecked ->
                                    // Update task item completion in database
                                    coroutineScope.launch {
                                        try {
                                            databaseHelper.toggleTaskItemCompletion(taskItemId, isChecked)

                                            // Update both allTasks and tasks lists
                                            allTasks = allTasks.map { currentTask ->
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

                                    // Update both allTasks and tasks lists
                                    allTasks = allTasks.filter { it.timestampMillis != task.timestampMillis }
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