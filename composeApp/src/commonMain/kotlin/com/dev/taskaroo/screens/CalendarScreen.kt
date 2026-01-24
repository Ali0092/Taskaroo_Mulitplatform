/**
 * Calendar-based task selection and viewing screen.
 *
 * This screen provides a calendar interface for selecting dates and viewing tasks
 * scheduled for specific dates. Tasks are organized by hourly time slots throughout
 * the day, making it easy to visualize the daily schedule. Users can interact with
 * tasks directly from the calendar view.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.common.DeleteConfirmationDialog
import com.dev.taskaroo.common.HorizontalCalendar
import com.dev.taskaroo.common.TaskCardConcise
import com.dev.taskaroo.common.TaskarooTopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.primaryLiteColorVariant
import com.dev.taskaroo.utils.DateTimeUtils
import com.dev.taskaroo.utils.Utils.hoursList
import com.dev.taskaroo.utils.todayDate
import kotlinx.coroutines.launch
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.add_icon

class CalendarScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current
        val coroutineScope = rememberCoroutineScope()

        // Selected date state
        var selectedDate by remember {
            mutableStateOf(todayDate())
        }

        // Tasks for selected date
        var tasksForSelectedDate by remember { mutableStateOf<List<TaskData>>(emptyList()) }

        // Delete dialog state
        var showDeleteDialog by remember { mutableStateOf(false) }
        var taskToDelete by remember { mutableStateOf<TaskData?>(null) }

        // Load tasks when date changes
        LaunchedEffect(selectedDate) {
            val startMillis = DateTimeUtils.getStartOfDayMillis(selectedDate)
            val endMillis = DateTimeUtils.getEndOfDayMillis(selectedDate)
            tasksForSelectedDate = databaseHelper.getTasksForDate(startMillis, endMillis)
        }

        // Group tasks by hour slot
        fun groupTasksByHour(tasks: List<TaskData>): Map<Int, List<TaskData>> {
            return tasks
                .groupBy { DateTimeUtils.getHourSlotIndex(it.timestampMillis) }
                .mapValues { (_, tasksInHour) ->
                    tasksInHour.sortedBy { it.timestampMillis }  // Ascending order
                }
        }

        Scaffold { innerPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TaskarooTopAppBar(
                    title = "Schedule",
                    canShowNavigationIcon = false,
                    otherIcon = Res.drawable.add_icon,
                    onOtherIconClick = {
                        navigator.push(CreateTaskScreen())
                    }
                )

                HorizontalCalendar { newSelectedDate ->
                    selectedDate = newSelectedDate
                    println("Selected date: $selectedDate")
                }

                Text(
                    text = "Today's todo list",
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                // Group tasks by hour
                val tasksByHour = remember(tasksForSelectedDate) {
                    groupTasksByHour(tasksForSelectedDate)
                }

                Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().verticalScroll(rememberScrollState())) {
                    hoursList.forEachIndexed { index, hourString ->
                        val tasksForThisHour = tasksByHour[index] ?: emptyList()
                        HourColumnItem(
                            hour = hourString,
                            items = tasksForThisHour,
                            onTaskItemToggle = { taskItemId, isChecked ->
                                coroutineScope.launch {
                                    try {
                                        databaseHelper.toggleTaskItemCompletion(taskItemId, isChecked)

                                        // Refresh tasks for selected date
                                        val startMillis = DateTimeUtils.getStartOfDayMillis(selectedDate)
                                        val endMillis = DateTimeUtils.getEndOfDayMillis(selectedDate)
                                        tasksForSelectedDate = databaseHelper.getTasksForDate(startMillis, endMillis)
                                    } catch (e: Exception) {
                                        println("CalendarScreen: Error toggling task item - ${e.message}")
                                    }
                                }
                            },
                            onTaskClick = { taskData ->
                                navigator.push(PreviewTaskScreen(taskTimestampToEdit = taskData.timestampMillis))
                            },
                            onTaskLongClick = { taskData ->
                                taskToDelete = taskData
                                showDeleteDialog = true
                            }
                        )
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

                                    // Refresh tasks for selected date
                                    val startMillis = DateTimeUtils.getStartOfDayMillis(selectedDate)
                                    val endMillis = DateTimeUtils.getEndOfDayMillis(selectedDate)
                                    tasksForSelectedDate = databaseHelper.getTasksForDate(startMillis, endMillis)

                                    showDeleteDialog = false
                                    taskToDelete = null
                                }
                            } catch (e: Exception) {
                                println("CalendarScreen: Error deleting task - ${e.message}")
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

/**
 * Displays a single hour time slot with its associated tasks.
 *
 * This composable renders a horizontal layout with the hour label on the left
 * and a list of tasks scheduled for that hour on the right. A divider line
 * separates the content visually.
 *
 * @param hour The formatted hour string (e.g., "09:00\nAM")
 * @param items List of tasks scheduled for this hour slot
 * @param modifier Modifier for the column layout
 * @param onTaskItemToggle Callback when a task item's completion status is toggled
 * @param onTaskClick Callback when a task is clicked
 * @param onTaskLongClick Callback when a task is long-pressed
 */
@Composable
fun HourColumnItem(
    hour: String,
    items: List<TaskData>,
    modifier: Modifier = Modifier,
    onTaskItemToggle: (String, Boolean) -> Unit = { _, _ -> },
    onTaskClick: (TaskData) -> Unit = {},
    onTaskLongClick: (TaskData) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = hour,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End
            )


            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                Spacer(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .height(1.dp)
                        .fillMaxWidth(1f)
                        .background(primaryLiteColorVariant)
                )
                // Render all tasks for this hour slot
                items.forEach { taskData ->
                    TaskCardConcise(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        taskData = taskData,
                        onTaskItemToggle = onTaskItemToggle,
                        onClick = { onTaskClick(taskData) },
                        onLongClick = { onTaskLongClick(taskData) }
                    )
                }
            }

    }


}
}
