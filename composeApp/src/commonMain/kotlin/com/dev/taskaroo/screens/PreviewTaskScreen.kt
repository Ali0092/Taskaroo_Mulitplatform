/**
 * Task preview/details screen for viewing task information.
 *
 * This screen displays task details in a read-only format, allowing users to
 * view complete task information without accidentally editing. Users can mark
 * task items as complete directly from this screen. Edit and Delete actions
 * are available via top bar buttons.
 *
 * @author Muhammad Ali
 * @date 2026-01-05
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.common.DeleteConfirmationDialog
import com.dev.taskaroo.common.TaskItemRow
import com.dev.taskaroo.common.TaskStatusBadge
import com.dev.taskaroo.common.TopAppBar
import com.dev.taskaroo.common.toBoolean
import com.dev.taskaroo.common.toTaskStatus
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.highPriorityBackground
import com.dev.taskaroo.highPriorityColor
import com.dev.taskaroo.lowPriorityBackground
import com.dev.taskaroo.lowPriorityColor
import com.dev.taskaroo.mediumPriorityBackground
import com.dev.taskaroo.mediumPriorityColor
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.notifications.rememberNotificationScheduler
import com.dev.taskaroo.urgentPriorityBackground
import com.dev.taskaroo.urgentPriorityColor
import com.dev.taskaroo.utils.DateTimeUtils.isTaskOverdue
import com.dev.taskaroo.utils.formatDateDisplay
import com.dev.taskaroo.utils.formatTimeDisplay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.calendar
import taskaroo.composeapp.generated.resources.clock
import taskaroo.composeapp.generated.resources.delete_icon
import taskaroo.composeapp.generated.resources.edit_icon
import kotlin.time.ExperimentalTime

/**
 * Screen for previewing task details with edit and delete options.
 *
 * Provides a read-only view of task information including:
 * - Priority badge with colored indicator
 * - Deadline date and time
 * - Task title and description
 * - Task items checklist (interactive - can be toggled)
 * - Progress indicator showing completion percentage
 *
 * Top bar actions:
 * - Edit button: Navigate to CreateTaskScreen in edit mode
 * - Delete button: Show confirmation dialog, then delete task
 *
 * @property taskTimestampToEdit Timestamp of the task to preview
 */
class PreviewTaskScreen(
    private val taskTimestampToEdit: Long
) : Screen {

    @OptIn(ExperimentalTime::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        val notificationScheduler = rememberNotificationScheduler()

        var taskData by remember { mutableStateOf<TaskData?>(null) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(true) }

        // Load task data from database
        LaunchedEffect(taskTimestampToEdit) {
            try {
                isLoading = true
                taskData = databaseHelper.getTaskByTimestamp(taskTimestampToEdit)
                isLoading = false
            } catch (e: Exception) {
                println("PreviewTaskScreen: Error loading task - ${e.message}")
                isLoading = false
                navigator.pop() // Go back if task not found
            }
        }

        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top App Bar with Edit and Delete buttons
                TopAppBar(
                    title = "Task Details",
                    canShowNavigationIcon = true,
                    otherIcon = Res.drawable.edit_icon,
                    secondIcon = Res.drawable.delete_icon,
                    onBackButtonClick = {
                        navigator.pop()
                    },
                    onOtherIconClick = {
                        // Navigate to CreateTaskScreen in edit mode
                        navigator.push(CreateTaskScreen(taskTimestampToEdit = taskTimestampToEdit))
                    },
                    onSecondIconClick = {
                        // Show delete confirmation dialog
                        showDeleteDialog = true
                    }
                )

                // Display task information if loaded
                taskData?.let { task ->
                    // Status Section
                    TaskStatusBadge(
                        modifier = Modifier.fillMaxWidth(),
                        status = task.isDone.toTaskStatus(),
                        isOverdue = isTaskOverdue(task.timestampMillis),
                        onStatusChange = { newStatus ->
                            val isDone = newStatus.toBoolean()
                            coroutineScope.launch {
                                try {
                                    databaseHelper.updateTaskDoneStatus(task.timestampMillis, isDone)

                                    // Cancel notification if task marked done and is a meeting
                                    if (isDone && task.isMeeting) {
                                        notificationScheduler.cancelNotification(task.timestampMillis)
                                    }

                                    // Update local state
                                    taskData = task.copy(isDone = isDone)
                                } catch (e: Exception) {
                                    println("Error updating task status: ${e.message}")
                                }
                            }
                        },
                        fullWidth = true
                    )

                    // Priority Badge
                    val (priorityColor) = when (task.category.lowercase()) {
                        "urgent" -> urgentPriorityColor to urgentPriorityBackground
                        "high" -> highPriorityColor to highPriorityBackground
                        "medium" -> mediumPriorityColor to mediumPriorityBackground
                        "low" -> lowPriorityColor to lowPriorityBackground
                        else -> Color.Gray to Color.LightGray
                    }

                    // Deadline Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        // Date
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.calendar),
                                contentDescription = "Calendar",
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = task.timestampMillis.formatDateDisplay(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                        }

                        // Time
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.clock),
                                contentDescription = "Clock",
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = task.timestampMillis.formatTimeDisplay(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Priority indicator dot
                                Card(
                                    modifier = Modifier.size(8.dp),
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = priorityColor)
                                ) {}

                                // Priority text
                                Text(
                                    text = task.category.replaceFirstChar { it.uppercase() },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = priorityColor
                                )
                            }
                        }
                    }

                    // Task Title
                    Text(
                        text = task.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Task Description/Subtitle
                    if (task.subtitle.isNotBlank()) {
                        Text(
                            text = task.subtitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Task Items Checklist
                    if (task.taskList.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Sub-tasks list",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                            )

                            task.taskList.forEach { taskItem ->
                                TaskItemRow(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    taskItem = taskItem,
                                    maxLines = 20,
                                    onToggle = { isCompleted ->
                                        coroutineScope.launch {
                                            databaseHelper.toggleTaskItemCompletion(taskItem.id, isCompleted)
                                            // Reload task data to update progress
                                            taskData = databaseHelper.getTaskByTimestamp(taskTimestampToEdit)
                                        }
                                    }
                                )
                            }

                        }
                    }

                    // Add some bottom spacing
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Show loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading task...",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        DeleteConfirmationDialog(
            showDialog = showDeleteDialog,
            taskTitle = taskData?.title ?: "Task",
            onConfirm = {
                coroutineScope.launch {
                    try {
                        databaseHelper.deleteTask(taskTimestampToEdit)
                        showDeleteDialog = false
                        navigator.pop() // Go back after deletion
                    } catch (e: Exception) {
                        println("PreviewTaskScreen: Error deleting task - ${e.message}")
                        showDeleteDialog = false
                    }
                }
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}
