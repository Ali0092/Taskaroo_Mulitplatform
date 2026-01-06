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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.common.CapsuleFloatingActionButton
import com.dev.taskaroo.common.DeleteConfirmationDialog
import com.dev.taskaroo.common.TaskCard
import com.dev.taskaroo.common.TaskChipRow
import com.dev.taskaroo.common.TopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.preferences.AppSettings
import com.dev.taskaroo.preferences.ThemeMode
import com.dev.taskaroo.preferences.getPreferencesManager
import com.dev.taskaroo.utils.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.no_data_placeholder
import taskaroo.composeapp.generated.resources.settings_icon
import taskaroo.composeapp.generated.resources.theme_icon

/**
 * Main screen displaying task list with filtering and management capabilities.
 *
 * This screen provides a comprehensive task management interface that allows users to:
 * - View tasks filtered by "Upcoming" (future tasks) or "All" (all tasks)
 * - Toggle task item completion status with real-time database updates
 * - Navigate to task creation via floating action button
 * - Edit tasks by tapping on them
 * - Delete tasks via long-press with confirmation dialog
 * - See completion progress for each task
 *
 * The screen maintains filter state across navigation and automatically refreshes
 * task data when returning from other screens.
 */
class MainScreen : Screen {

    @Composable
    override fun Content() {
        // Sample task data with all priority levels

        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()

        val preferencesManager = remember { getPreferencesManager() }
        val settings by preferencesManager.settingsFlow.collectAsState(AppSettings())

        var showThemeDialog by remember { mutableStateOf(false) }

        // Filter state - persists during navigation session
        var selectedFilter by rememberSaveable { mutableStateOf("Active") }

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
                "All" -> {
                    allTasks.sortedBy { it.timestampMillis }
                }

                "Active" -> {
                    allTasks
                        .filter { !it.isDone }
                        .sortedBy { it.timestampMillis }
                }

                "Completed" -> {
                    allTasks
                        .filter { it.isDone }
                        .sortedBy { it.timestampMillis }
                }

                "Upcoming" -> {
                    allTasks
                        .filter { it.timestampMillis >= currentTime }
                        .sortedBy { it.timestampMillis }
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPaddings)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {

                TopAppBar(
                    title = "Make every day count forward✨",
                    canShowNavigationIcon = false,
                    otherIcon = Res.drawable.theme_icon,
                    onOtherIconClick = {
                        showThemeDialog = true
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Show filter chips only when there are tasks
                if (allTasks.isNotEmpty()) {
                    TaskChipRow(
                        categories = listOf("Active", "All", "Completed", "Upcoming"),
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
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
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
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No tasks! Tap the button at\nbottom right to create one.",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
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
                                onTaskDoneToggle = { isDone ->
                                    coroutineScope.launch {
                                        try {
                                            databaseHelper.updateTaskDoneStatus(task.timestampMillis, isDone)

                                            allTasks = allTasks.map { currentTask ->
                                                if (currentTask.timestampMillis == task.timestampMillis) {
                                                    currentTask.copy(isDone = isDone)
                                                } else {
                                                    currentTask
                                                }
                                            }
                                        } catch (e: Exception) {
                                            println("MainScreen: Error toggling task done status - ${e.message}")
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

        if (showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = settings.themeMode,
                onThemeSelected = { theme ->
                    showThemeDialog = false
                    // Update theme immediately
                    CoroutineScope(Dispatchers.Default).launch {
                        preferencesManager.updateThemeMode(theme)
                    }
                },
                onDismiss = { showThemeDialog = false }
            )
        }

    }
    @Composable
    fun ThemeSelectionDialog(
        currentTheme: ThemeMode,
        onThemeSelected: (ThemeMode) -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Choose theme")
            },
            text = {
                Column(modifier = Modifier.selectableGroup()) {
                    ThemeMode.entries.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = currentTheme == theme,
                                    onClick = { onThemeSelected(theme) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == theme,
                                onClick = { onThemeSelected(theme) }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = when (theme) {
                                    ThemeMode.SYSTEM -> "System default"
                                    ThemeMode.LIGHT -> "Light"
                                    ThemeMode.DARK -> "Dark"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }


}