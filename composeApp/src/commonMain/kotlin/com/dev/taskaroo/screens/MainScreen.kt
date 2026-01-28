/**
 * Main task list screen with filtering and task management.
 *
 * This screen serves as the primary interface for viewing and managing tasks.
 * It provides filtering capabilities (Upcoming/All), task completion tracking,
 * and navigation to create new tasks or edit existing ones. Tasks can be deleted
 * via long-press interaction.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.common.DeleteConfirmationDialog
import com.dev.taskaroo.common.TaskCard
import com.dev.taskaroo.common.TaskSummaryCards
import com.dev.taskaroo.common.TaskarooTopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.notifications.rememberNotificationPermissionRequester
import com.dev.taskaroo.notifications.rememberNotificationScheduler
import com.dev.taskaroo.preferences.AppSettings
import com.dev.taskaroo.preferences.ThemeMode
import com.dev.taskaroo.preferences.getPreferencesManager
import com.dev.taskaroo.utils.DateTimeUtils.isTaskOverdue
import com.dev.taskaroo.utils.currentTimeMillis
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.edit_icon
import taskaroo.composeapp.generated.resources.no_data_placeholder
import taskaroo.composeapp.generated.resources.no_task
import taskaroo.composeapp.generated.resources.settings_icon

class MainScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()

        val preferencesManager = remember { getPreferencesManager() }
        val settings by preferencesManager.settingsFlow.collectAsState(AppSettings())
        val notificationScheduler = rememberNotificationScheduler()

        // Permission requester for notifications
        val requestNotificationPermission = rememberNotificationPermissionRequester { isGranted ->
            if (isGranted) {
                println("MainScreen: Notification permission granted")
            } else {
                println("MainScreen: Notification permission denied")
            }
        }

        // Request notification permission on first launch
        LaunchedEffect(Unit) {
            val hasPermission = notificationScheduler.checkPermissionStatus()
            if (!hasPermission) {
                requestNotificationPermission()
            } else {
                println("MainScreen: Notification permission already granted")
            }
        }

        // Track all tasks and filter state
        var allTasks by remember { mutableStateOf<List<TaskData>>(emptyList()) }
        var selectedFilter by remember { mutableStateOf("Active") }
        var filteredTasks by remember { mutableStateOf<List<TaskData>>(emptyList()) }
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

        // Filter tasks based on selected filter
        LaunchedEffect(selectedFilter, allTasks) {
            val currentTime = currentTimeMillis()
            
            filteredTasks = when (selectedFilter) {
                "All" -> allTasks.sortedBy { it.timestampMillis }
                "Completed" -> allTasks.filter { it.isDone }.sortedBy { it.timestampMillis }
                "Active" -> allTasks
                    .filter { !it.isDone && it.timestampMillis >= currentTime }
                    .sortedBy { it.timestampMillis }
                "Overdue" -> allTasks
                    .filter { !it.isDone && isTaskOverdue(it.timestampMillis) }
                    .sortedBy { it.timestampMillis }
                else -> allTasks.sortedBy { it.timestampMillis }
            }
        }


        Scaffold { innerPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            ) {

                TaskarooTopAppBar(
                    title = "From to-do to done✨",
                    canShowNavigationIcon = false,
                    otherIcon = Res.drawable.edit_icon,
                    trailingIcon = Res.drawable.settings_icon,
                    onOtherIconClick = {
                        navigator.push(NotesScreen())
                    },
                    onTrailingIconClick = {
                        navigator.push(SettingsScreen())
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Show summary cards only when there are tasks
                if (allTasks.isNotEmpty()) {
                    val currentTime = currentTimeMillis()
                    val totalTasks = allTasks.size
                    val completedTasks = allTasks.count { it.isDone }
                    val activeTasks = allTasks.count { !it.isDone }
                    val overdueTasks = allTasks.count { !it.isDone && isTaskOverdue(it.timestampMillis) }
                    
                    TaskSummaryCards(
                        totalTasks = totalTasks,
                        completedTasks = completedTasks,
                        activeTasks = activeTasks,
                        overdueTasks = overdueTasks,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter ->
                            selectedFilter = filter
                        }
                    )
                    
                    Spacer(Modifier.height(24.dp))
                }

                // Dynamic section title - only show when there are filtered tasks
                if (allTasks.isNotEmpty() && filteredTasks.isNotEmpty()) {
                    Text(
                        text = when (selectedFilter) {
                            "All" -> "All Tasks"
                            "Completed" -> "Completed Tasks"
                            "Active" -> "Active Tasks"
                            "Overdue" -> "Overdue Tasks"
                            else -> "Recent Tasks"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
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
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else if (filteredTasks.isEmpty() && allTasks.isNotEmpty()) {
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
                                painter = painterResource(Res.drawable.no_task),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No ${selectedFilter.lowercase()} tasks",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (allTasks.isEmpty()) {
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
                                painter = painterResource(Res.drawable.no_task),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No tasks yet\nadd one to get started",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // LazyColumn with task cards
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        items(filteredTasks, key = { it.timestampMillis }) { task ->
                            TaskCard(
                                taskData = task,
                                onTaskDoneToggle = { isDone ->
                                    coroutineScope.launch {
                                        try {
                                            databaseHelper.updateTaskDoneStatus(task.timestampMillis, isDone)

                                            // Cancel notification if task marked done and is a meeting
                                            if (isDone && task.isMeeting) {
                                                notificationScheduler.cancelNotification(task.timestampMillis)
                                            }

                                            allTasks = allTasks.map { currentTask ->
                                                if (currentTask.timestampMillis == task.timestampMillis) {
                                                    currentTask.copy(isDone = isDone)
                                                } else {
                                                    currentTask
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                },
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

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                },
                                onClick = {
                                    navigator.push(PreviewTaskScreen(taskTimestampToEdit = task.timestampMillis))
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

                                    // Update allTasks list
                                    allTasks = allTasks.filter { it.timestampMillis != task.timestampMillis }

                                    showDeleteDialog = false
                                    taskToDelete = null
                                }
                            } catch (e: Exception) {
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