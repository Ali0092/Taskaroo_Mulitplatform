/**
 * Task creation and editing screen with priority selection.
 *
 * This screen serves as a comprehensive task creation and editing interface.
 * It supports both creating new tasks and editing existing ones, with features
 * including priority selection, deadline setting, time selection, and dynamic
 * task item checklist management. The screen intelligently preserves task item
 * completion status when editing existing tasks.
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.common.DeleteConfirmationDialog
import com.dev.taskaroo.common.TaskarooTopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import com.dev.taskaroo.notifications.rememberNotificationScheduler
import com.dev.taskaroo.preferences.AppSettings
import com.dev.taskaroo.preferences.getPreferencesManager
import com.dev.taskaroo.primary
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.primaryLiteColorVariant
import com.dev.taskaroo.utils.NativeDatePicker
import com.dev.taskaroo.utils.NativeTimePicker
import com.dev.taskaroo.utils.Utils.priorities
import com.dev.taskaroo.utils.currentTimeMillis
import com.dev.taskaroo.utils.todayDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.calendar
import taskaroo.composeapp.generated.resources.clock
import taskaroo.composeapp.generated.resources.close_icon
import taskaroo.composeapp.generated.resources.delete_icon
import taskaroo.composeapp.generated.resources.tick_icon
import kotlin.time.ExperimentalTime


class CreateTaskScreen(
    private val taskTimestampToEdit: Long? = null
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current

        // Determine if we're in edit mode
        val isEditMode = taskTimestampToEdit != null
        var existingTask by remember { mutableStateOf<TaskData?>(null) }

        // Load existing task if in edit mode
        LaunchedEffect(taskTimestampToEdit) {
            if (taskTimestampToEdit != null) {
                try {
                    existingTask = databaseHelper.getTaskByTimestamp(taskTimestampToEdit)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Form states
        var taskTitle by remember { mutableStateOf("") }
        var taskDescription by remember { mutableStateOf("") }
        var selectedPriority by remember { mutableStateOf("Medium") }
        var selectedDate by remember {
            val today = todayDate()
            mutableStateOf("${today.year}-${today.monthNumber.toString().padStart(2, '0')}-${today.dayOfMonth.toString().padStart(2, '0')}")
        }
        // Default time is 1 hour ahead of current time
        var selectedHour by remember {
            val now = Instant.fromEpochMilliseconds(currentTimeMillis())
            val currentDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
            val oneHourLater = currentDateTime.hour + 1
            val hour24 = if (oneHourLater >= 24) oneHourLater - 24 else oneHourLater
            val (hour12, _) = when {
                hour24 == 0 -> Pair(12, "AM")
                hour24 < 12 -> Pair(hour24, "AM")
                hour24 == 12 -> Pair(12, "PM")
                else -> Pair(hour24 - 12, "PM")
            }
            mutableStateOf(hour12)
        }
        var selectedMinute by remember {
            val now = Instant.fromEpochMilliseconds(currentTimeMillis())
            val currentDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
            mutableStateOf(currentDateTime.minute)
        }
        var selectedAmPm by remember {
            val now = Instant.fromEpochMilliseconds(currentTimeMillis())
            val currentDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
            val oneHourLater = currentDateTime.hour + 1
            val hour24 = if (oneHourLater >= 24) oneHourLater - 24 else oneHourLater
            val (_, amPm) = when {
                hour24 == 0 -> Pair(12, "AM")
                hour24 < 12 -> Pair(hour24, "AM")
                hour24 == 12 -> Pair(12, "PM")
                else -> Pair(hour24 - 12, "PM")
            }
            mutableStateOf(amPm)
        }
        var isMeetingTask by remember { mutableStateOf(false) } // Meeting toggle
        var meetingLink by remember { mutableStateOf("") }       // Meeting link field

        // Picker dialog states
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }

        // Task details checklist
        var taskDetailItems by remember { mutableStateOf(listOf("")) }
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val notificationScheduler = rememberNotificationScheduler()

        // Settings state for notification preferences
        val preferencesManager = remember { getPreferencesManager() }
        val settings by preferencesManager.settingsFlow.collectAsState(AppSettings())

        // Error handling and loading state
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isSaving by remember { mutableStateOf(false) }

        // Delete dialog state
        var showDeleteDialog by remember { mutableStateOf(false) }

        // Conversion utility functions
        fun convertTo24Hour(hour12: Int, amPm: String): Int {
            return when {
                amPm == "AM" && hour12 == 12 -> 0
                amPm == "AM" -> hour12
                amPm == "PM" && hour12 == 12 -> 12
                else -> hour12 + 12
            }
        }

        fun convertTo12Hour(hour24: Int): Pair<Int, String> {
            return when {
                hour24 == 0 -> Pair(12, "AM")
                hour24 < 12 -> Pair(hour24, "AM")
                hour24 == 12 -> Pair(12, "PM")
                else -> Pair(hour24 - 12, "PM")
            }
        }

        fun formatDateDisplay(dateString: String): String {
            // Convert "2024-12-15" to "Dec 15, 2024"
            val parts = dateString.split("-")
            if (parts.size != 3) return dateString
            val year = parts[0]
            val month = parts[1].toIntOrNull() ?: return dateString
            val day = parts[2].toIntOrNull() ?: return dateString

            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            return "${monthNames.getOrNull(month - 1) ?: month} $day, $year"
        }

        fun formatTimeDisplay(hour: Int, minute: Int, amPm: String): String {
            return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
        }

        // Pre-fill form when task is loaded in edit mode
        LaunchedEffect(existingTask) {
            existingTask?.let { task ->
                taskTitle = task.title
                taskDescription = task.subtitle
                selectedPriority = task.category
                isMeetingTask = task.isMeeting
                meetingLink = task.meetingLink

                // Extract date and time from timestamp
                val instant = Instant.fromEpochMilliseconds(task.timestampMillis)
                val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                selectedDate = "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')}"

                // Convert 24-hour to 12-hour format
                val (hour12, amPm) = convertTo12Hour(dateTime.hour)
                selectedHour = hour12
                selectedMinute = dateTime.minute
                selectedAmPm = amPm

                // Pre-fill checklist items
                taskDetailItems = if (task.taskList.isEmpty()) {
                    listOf("")
                } else {
                    task.taskList.map { it.text }
                }
            }
        }

        Scaffold { innerPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPaddings)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                TaskarooTopAppBar(
                    title = if (isEditMode) "Edit your Task" else "Make your Task",
                    canShowNavigationIcon = true,
                    otherIcon = Res.drawable.tick_icon,
                    trailingIcon = if (isEditMode) Res.drawable.delete_icon else null,
                    onBackButtonClick = {
                        navigator.pop()
                    },
                    onTrailingIconClick = {
                        if (isEditMode) {
                            showDeleteDialog = true
                        }
                    },
                    onOtherIconClick = {
                        if (taskTitle.isNotBlank() && selectedDate.isNotBlank()) {
                            if (isSaving) return@TaskarooTopAppBar  // Prevent double-clicks

                            // Validate meeting link if provided
                            if (isMeetingTask && meetingLink.trim().isNotEmpty()) {
                                val urlPattern = "^(https?://|www\\.).+".toRegex(RegexOption.IGNORE_CASE)
                                if (!meetingLink.trim().matches(urlPattern)) {
                                    errorMessage = "Please enter a valid URL for the meeting link (e.g., https://zoom.us/j/...)"
                                    return@TaskarooTopAppBar
                                }
                            }

                            coroutineScope.launch {
                                try {
                                    isSaving = true
                                    errorMessage = null

                                    // Parse date from selectedDate string (format: YYYY-MM-DD)
                                    val dateParts = selectedDate.split("-")
                                    if (dateParts.size == 3) {
                                        val year = dateParts[0].toIntOrNull() ?: run {
                                            errorMessage = "Invalid year in date"
                                            isSaving = false
                                            return@launch
                                        }
                                        val month = dateParts[1].toIntOrNull() ?: run {
                                            errorMessage = "Invalid month in date"
                                            isSaving = false
                                            return@launch
                                        }
                                        val day = dateParts[2].toIntOrNull() ?: run {
                                            errorMessage = "Invalid day in date"
                                            isSaving = false
                                            return@launch
                                        }

                                        // Validate date components
                                        if (month !in 1..12) {
                                            errorMessage = "Month must be between 1-12"
                                            isSaving = false
                                            return@launch
                                        }
                                        if (day !in 1..31) {
                                            errorMessage = "Day must be between 1-31"
                                            isSaving = false
                                            return@launch
                                        }

                                        // Create LocalDateTime with selected date and time
                                        // Convert 12-hour format to 24-hour for storage
                                        val hour24 = convertTo24Hour(selectedHour, selectedAmPm)
                                        val localDateTime = LocalDateTime(
                                            year, month, day,
                                            hour24, selectedMinute, 0, 0
                                        )

                                        // Convert to timestamp in milliseconds (or use existing in edit mode)
                                        val timestampMillis = if (isEditMode) {
                                            taskTimestampToEdit!!  // Use existing timestamp in edit mode
                                        } else {
                                            localDateTime
                                                .toInstant(TimeZone.currentSystemDefault())
                                                .toEpochMilliseconds()
                                        }


                                        // Create task items from checklist
                                        val taskItems = if (isEditMode) {
                                            // In edit mode, preserve existing task item IDs and completion status
                                            val existingItems = existingTask?.taskList ?: emptyList()
                                            taskDetailItems
                                                .filter { it.isNotBlank() }
                                                .mapIndexed { index, text ->
                                                    // Try to find matching existing item by index or text
                                                    val existingItem = existingItems.getOrNull(index)
                                                    val itemId = existingItem?.id ?: "${timestampMillis}_item_$index"
                                                    val isCompleted = if (existingItem?.text == text) {
                                                        existingItem.isCompleted
                                                    } else {
                                                        false  // New or modified items are not completed
                                                    }

                                                    TaskItem(
                                                        id = itemId,
                                                        text = text,
                                                        isCompleted = isCompleted
                                                    )
                                                }
                                        } else {
                                            // Create mode - all new items
                                            taskDetailItems
                                                .filter { it.isNotBlank() }
                                                .mapIndexed { index, text ->
                                                    TaskItem(
                                                        id = "${timestampMillis}_item_$index",
                                                        text = text,
                                                        isCompleted = false
                                                    )
                                                }
                                        }

                                        val completedCount = taskItems.count { it.isCompleted }

                                        // Create task data
                                        val taskData = TaskData(
                                            timestampMillis = timestampMillis,
                                            title = taskTitle,
                                            subtitle = taskDescription,
                                            category = selectedPriority,
                                            taskList = taskItems,
                                            completedTasks = completedCount,
                                            isMeeting = isMeetingTask,
                                            meetingLink = if (isMeetingTask) meetingLink.trim() else ""
                                        )

                                        // Save to database
                                        if (isEditMode) {
                                            databaseHelper.updateTask(taskData)
                                        } else {
                                            databaseHelper.insertTask(taskData)
                                        }

                                        // Handle notification scheduling
                                        try {
                                            // Always cancel existing notification
                                            notificationScheduler.cancelNotification(taskData.timestampMillis)

                                            // Reschedule if not done
                                            if (!taskData.isDone) {
                                                val hasPermission = notificationScheduler.checkPermissionStatus()
                                                if (hasPermission) {
                                                    // Use universal scheduling that respects notification preferences
                                                    notificationScheduler.scheduleTaskNotification(taskData, settings.notificationsEnabled)
                                                } else {
                                                    println("CreateTask: No notification permission")
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                        // Navigate back only on success
                                        if (isEditMode) {
                                            // Pop twice: CreateTaskScreen -> PreviewTaskScreen -> MainScreen
                                            navigator.pop()
                                            navigator.pop()
                                        } else {
                                            // Pop once for creating new task
                                            navigator.pop()
                                        }
                                    } else {
                                        errorMessage = "Invalid date format. Use YYYY-MM-DD"
                                        isSaving = false
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    errorMessage = "Failed to save task: ${e.message}"
                                    isSaving = false
                                }
                            }
                        } else {
                            // Show validation error
                            errorMessage = when {
                                taskTitle.isBlank() -> "Please enter a task title"
                                selectedDate.isBlank() -> "Please enter a deadline date"
                                else -> "Please fill in required fields"
                            }
                        }
                    }
                )

                // Error message display
                errorMessage?.let { message ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFCDD2), // Light red background
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = message,
                            color = Color(0xFFC62828), // Dark red text
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Meeting Toggle
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Meeting?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Switch(
                            checked = isMeetingTask,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    // When enabling meeting toggle, check if permission is granted
                                    coroutineScope.launch {
                                        val hasPermission = notificationScheduler.checkPermissionStatus()
                                        if (hasPermission) {
                                            // Have permission, enable the toggle
                                            println("CreateTask: Permission granted, enabling meeting toggle")
                                            isMeetingTask = true
                                        } else {
                                            // No permission, show warning and don't enable
                                            println("CreateTask: Permission not granted, cannot enable meeting")
                                            errorMessage = "Please enable notification permission in Settings to use meeting reminders"
                                            isMeetingTask = false
                                        }
                                    }
                                } else {
                                    // Disabling meeting toggle
                                    isMeetingTask = false
                                    errorMessage = null // Clear any error messages
                                }
                            }
                        )

                    }
                }

                // Priority Selection - Moved to top
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Priority",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        priorities.forEach { priority ->
                            val isSelected = selectedPriority == priority
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .background(
                                        color = if (isSelected) primaryLiteColorVariant else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) primaryLiteColorVariant else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedPriority = priority
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = priority,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) primaryColorVariant else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                // Date and Time Selection - Combined Row
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Deadline & Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date Field with Calendar Icon
                        OutlinedTextField(
                            value = formatDateDisplay(selectedDate),
                            onValueChange = { },
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (!isEditMode) {
                                        showDatePicker = true
                                    }
                                },
                            enabled = false,
                            leadingIcon = {
                                Image(
                                    painter = painterResource(Res.drawable.calendar),
                                    contentDescription = "Calendar",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = if (isEditMode)
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.onBackground,
                                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                disabledLeadingIconColor = primaryColorVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Time Field with Clock Icon
                        OutlinedTextField(
                            value = formatTimeDisplay(selectedHour, selectedMinute, selectedAmPm),
                            onValueChange = { },
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (!isEditMode) {
                                        showTimePicker = true
                                    }
                                },
                            enabled = false,
                            leadingIcon = {
                                Image(
                                    painter = painterResource(Res.drawable.clock),
                                    contentDescription = "Clock",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = if (isEditMode)
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.onBackground,
                                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                disabledLeadingIconColor = primaryColorVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Task Title
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Task Title",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Enter task title",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Meeting Link (only show if isMeeting is true)
                if (isMeetingTask) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Meeting Link (Optional)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        OutlinedTextField(
                            value = meetingLink,
                            onValueChange = { meetingLink = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Enter meeting link (e.g., Zoom, Google Meet)",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                cursorColor = MaterialTheme.colorScheme.onBackground
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }

                // Task Description
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedTextField(
                        value = taskDescription,
                        onValueChange = { taskDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = {
                            Text(
                                text = "Enter task description",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 15,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Task Details Checklist
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tasks List",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        TextButton(
                            onClick = {
                                // Check if the last item is not empty before adding a new one
                                val lastItem = taskDetailItems.lastOrNull()
                                if (lastItem != null && lastItem.isNotBlank()) {
                                    taskDetailItems = taskDetailItems + ""
                                    // Scroll to bottom after adding new item
                                    coroutineScope.launch {
                                        delay(100) // Small delay to ensure layout is updated
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "+ Add Item",
                                fontSize = 14.sp,
                                color = primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        taskDetailItems.forEachIndexed { index, item ->
                            if (index < taskDetailItems.size) {
                                var itemText by remember(item) { mutableStateOf(item) }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = itemText,
                                        onValueChange = { newText ->
                                            itemText = newText
                                            val newItems = taskDetailItems.toMutableList()
                                            if (index >= newItems.size) {
                                                repeat(index - newItems.size + 1) {
                                                    newItems.add("")
                                                }
                                            }
                                            newItems[index] = newText
                                            taskDetailItems = newItems
                                        },
                                        modifier = Modifier.weight(1f),
                                        placeholder = {
                                            Text(
                                                text = "Task item ${index + 1}",
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                            cursorColor = MaterialTheme.colorScheme.onBackground
                                        ),
                                        maxLines = 2,
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )

                                    if (taskDetailItems.size > 1) {
                                        Icon(
                                            modifier = Modifier.clickable {
                                                val newItems = taskDetailItems.toMutableList()
                                                if (index < newItems.size) {
                                                    newItems.removeAt(index)
                                                    taskDetailItems = newItems
                                                }
                                            }.size(18.dp),
                                            painter = painterResource(Res.drawable.close_icon),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Delete confirmation dialog (only in edit mode)
            if (isEditMode && existingTask != null) {
                DeleteConfirmationDialog(
                    showDialog = showDeleteDialog,
                    taskTitle = existingTask?.title ?: "",
                    onDismiss = {
                        showDeleteDialog = false
                    },
                    onConfirm = {
                        coroutineScope.launch {
                            try {
                                taskTimestampToEdit?.let { timestamp ->
                                    databaseHelper.deleteTask(timestamp)
                                    showDeleteDialog = false
                                    navigator.pop()  // Return to previous screen
                                }
                            } catch (e: Exception) {
                                println("DeleteTask: Error - ${e.message}")
                                errorMessage = "Failed to delete task: ${e.message}"
                                showDeleteDialog = false
                            }
                        }
                    }
                )
            }

            // Date Picker Dialog
            if (showDatePicker) {
                val dateParts = selectedDate.split("-")
                val initialYear = dateParts.getOrNull(0)?.toIntOrNull() ?: 2024
                val initialMonth = dateParts.getOrNull(1)?.toIntOrNull() ?: 1
                val initialDay = dateParts.getOrNull(2)?.toIntOrNull() ?: 1

                NativeDatePicker(
                    initialYear = initialYear,
                    initialMonth = initialMonth,
                    initialDay = initialDay,
                    onDateSelected = { year, month, day ->
                        selectedDate = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
                        showDatePicker = false
                    },
                    onDismiss = {
                        showDatePicker = false
                    }
                )
            }

            // Time Picker Dialog
            if (showTimePicker) {
                NativeTimePicker(
                    initialHour = selectedHour,
                    initialMinute = selectedMinute,
                    initialAmPm = selectedAmPm,
                    onTimeSelected = { hour, minute, amPm ->
                        selectedHour = hour
                        selectedMinute = minute
                        selectedAmPm = amPm
                        showTimePicker = false
                    },
                    onDismiss = {
                        showTimePicker = false
                    }
                )
            }
        }
    }
}