package com.dev.taskaroo.screens

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.common.TopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.primary
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.primaryLiteColorVariant
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
import taskaroo.composeapp.generated.resources.add_icon
import taskaroo.composeapp.generated.resources.close_icon
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
        var isLoadingTask by remember { mutableStateOf(isEditMode) }

        // Load existing task if in edit mode
        LaunchedEffect(taskTimestampToEdit) {
            if (taskTimestampToEdit != null) {
                try {
                    isLoadingTask = true
                    existingTask = databaseHelper.getTaskByTimestamp(taskTimestampToEdit)
                    isLoadingTask = false
                } catch (e: Exception) {
                    println("EditTask: Error loading task - ${e.message}")
                    isLoadingTask = false
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
        var selectedHour by remember { mutableStateOf(12) }  // 0-23
        var selectedMinute by remember { mutableStateOf(0) } // 0-59

        // Task details checklist
        var taskDetailItems by remember { mutableStateOf(listOf("")) }
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        // Error handling and loading state
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isSaving by remember { mutableStateOf(false) }

        // Pre-fill form when task is loaded in edit mode
        LaunchedEffect(existingTask) {
            existingTask?.let { task ->
                taskTitle = task.title
                taskDescription = task.subtitle
                selectedPriority = task.category

                // Extract date and time from timestamp
                val instant = Instant.fromEpochMilliseconds(task.timestampMillis)
                val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                selectedDate = "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')}"
                selectedHour = dateTime.hour
                selectedMinute = dateTime.minute

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
                    .background(backgroundColor)
                    .padding(innerPaddings)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                TopAppBar(
                    title = if (isEditMode) "Edit your Task" else "Make your Task",
                    canShowNavigationIcon = true,
                    otherIcon = Res.drawable.tick_icon,
                    onBackButtonClick = {
                        navigator.pop()
                    },
                    onOtherIconClick = {
                        if (taskTitle.isNotBlank() && selectedDate.isNotBlank()) {
                            if (isSaving) return@TopAppBar  // Prevent double-clicks

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
                                        val localDateTime = LocalDateTime(
                                            year, month, day,
                                            selectedHour, selectedMinute, 0, 0
                                        )

                                        // Convert to timestamp in milliseconds (or use existing in edit mode)
                                        val timestampMillis = if (isEditMode) {
                                            taskTimestampToEdit!!  // Use existing timestamp in edit mode
                                        } else {
                                            localDateTime
                                                .toInstant(TimeZone.currentSystemDefault())
                                                .toEpochMilliseconds()
                                        }

                                        println("${if (isEditMode) "EditTask" else "CreateTask"}: ${if (isEditMode) "Updating" else "Creating"} task with timestamp: $timestampMillis")

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

                                        println("${if (isEditMode) "EditTask" else "CreateTask"}: Task items count: ${taskItems.size}")

                                        val completedCount = taskItems.count { it.isCompleted }

                                        // Create task data
                                        val taskData = TaskData(
                                            timestampMillis = timestampMillis,
                                            title = taskTitle,
                                            subtitle = taskDescription,
                                            category = selectedPriority,
                                            taskList = taskItems,
                                            completedTasks = completedCount
                                        )

                                        println("${if (isEditMode) "EditTask" else "CreateTask"}: About to ${if (isEditMode) "update" else "insert"} task: ${taskData.title}")

                                        // Save to database
                                        if (isEditMode) {
                                            databaseHelper.updateTask(taskData)
                                            println("EditTask: Task updated successfully!")
                                        } else {
                                            databaseHelper.insertTask(taskData)
                                            println("CreateTask: Task inserted successfully!")
                                        }

                                        // Navigate back only on success
                                        navigator.pop()
                                    } else {
                                        errorMessage = "Invalid date format. Use YYYY-MM-DD"
                                        isSaving = false
                                    }
                                } catch (e: Exception) {
                                    println("CreateTask: ERROR - ${e.message}")
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

                // Priority Selection - Moved to top
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Priority",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = onBackgroundColor
                    )

                    val priorities = listOf("Urgent", "High", "Medium", "Low")

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
                                        color = if (isSelected) primaryLiteColorVariant else onBackgroundColor.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedPriority = priority
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = priority,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) primaryColorVariant else onBackgroundColor
                                )
                            }
                        }
                    }
                }

                // Deadline Selection
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Deadline",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = onBackgroundColor
                    )

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { if (!isEditMode) selectedDate = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isEditMode,
                        placeholder = {
                            Text(
                                text = "Enter deadline (e.g., 2024-12-15)",
                                color = onBackgroundColor.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = onBackgroundColor,
                            unfocusedBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                            cursorColor = onBackgroundColor,
                            disabledBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                            disabledTextColor = onBackgroundColor
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Time Selection
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = onBackgroundColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Hour picker
                        OutlinedTextField(
                            value = selectedHour.toString().padStart(2, '0'),
                            onValueChange = { value ->
                                if (!isEditMode) {
                                    value.toIntOrNull()?.let { hour ->
                                        if (hour in 0..23) selectedHour = hour
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isEditMode,
                            label = { Text("Hour (00-23)", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = onBackgroundColor,
                                unfocusedBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                                cursorColor = onBackgroundColor,
                                disabledBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                                disabledTextColor = onBackgroundColor
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Minute picker
                        OutlinedTextField(
                            value = selectedMinute.toString().padStart(2, '0'),
                            onValueChange = { value ->
                                if (!isEditMode) {
                                    value.toIntOrNull()?.let { minute ->
                                        if (minute in 0..59) selectedMinute = minute
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isEditMode,
                            label = { Text("Minute (00-59)", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = onBackgroundColor,
                                unfocusedBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                                cursorColor = onBackgroundColor,
                                disabledBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                                disabledTextColor = onBackgroundColor
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
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
                        color = onBackgroundColor
                    )

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Enter task title",
                                color = onBackgroundColor.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = onBackgroundColor,
                            unfocusedBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                            cursorColor = onBackgroundColor
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Task Description
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = onBackgroundColor
                    )

                    OutlinedTextField(
                        value = taskDescription,
                        onValueChange = { taskDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = {
                            Text(
                                text = "Enter task description",
                                color = onBackgroundColor.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = onBackgroundColor,
                            unfocusedBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                            cursorColor = onBackgroundColor
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Task Details Checklist
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Task Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = onBackgroundColor
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
                                var itemText by remember { mutableStateOf(item) }

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
                                                color = onBackgroundColor.copy(alpha = 0.5f)
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = onBackgroundColor,
                                            unfocusedBorderColor = onBackgroundColor.copy(alpha = 0.3f),
                                            cursorColor = onBackgroundColor
                                        ),
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
        }
    }
}