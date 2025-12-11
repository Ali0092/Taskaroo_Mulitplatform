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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.primary
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.primaryLiteColorVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.back_button
import taskaroo.composeapp.generated.resources.calendar
import taskaroo.composeapp.generated.resources.close_icon

class CreateTaskScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Form states
        var taskTitle by remember { mutableStateOf("") }
        var taskDescription by remember { mutableStateOf("") }
        var selectedPriority by remember { mutableStateOf("Medium") }
        var selectedDate by remember { mutableStateOf("Dec 11, 2024") }
        var showDatePicker by remember { mutableStateOf(false) }

        // Task details checklist
        var taskDetailItems by remember { mutableStateOf(listOf("")) }
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        // Date selection state
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        var selectedYear by remember { mutableStateOf(2024) }
        var selectedMonth by remember { mutableStateOf(11) } // December (0-indexed)
        var selectedDay by remember { mutableStateOf(11) }

        // Date picker dialog
        if (showDatePicker) {
            AlertDialog(
                onDismissRequest = { showDatePicker = false },
                title = {
                    Text(
                        text = "Select Date",
                        color = onBackgroundColor,
                        fontWeight = FontWeight.Medium
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Year selector
                        Text(
                            text = "Year: $selectedYear",
                            color = onBackgroundColor,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { selectedYear-- },
                                modifier = Modifier
                                    .background(primary.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Text("-", color = primary, fontWeight = FontWeight.Bold)
                            }
                            IconButton(
                                onClick = { selectedYear++ },
                                modifier = Modifier
                                    .background(primary.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Text("+", color = primary, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Month selector
                        Text(
                            text = "Month: ${months[selectedMonth]}",
                            color = onBackgroundColor,
                            fontWeight = FontWeight.Medium
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(months.size) { index ->
                                FilterChip(
                                    onClick = { selectedMonth = index },
                                    label = { Text(months[index]) },
                                    selected = selectedMonth == index
                                )
                            }
                        }

                        // Day selector
                        Text(
                            text = "Day: $selectedDay",
                            color = onBackgroundColor,
                            fontWeight = FontWeight.Medium
                        )
                        val daysInMonth = when (selectedMonth) {
                            1 -> if ((selectedYear % 4 == 0 && selectedYear % 100 != 0) || (selectedYear % 400 == 0)) 29 else 28
                            0, 2, 4, 6, 7, 9, 11 -> 31
                            else -> 30
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(daysInMonth) { day ->
                                FilterChip(
                                    onClick = { selectedDay = day + 1 },
                                    label = { Text("${day + 1}") },
                                    selected = selectedDay == day + 1
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedDate = "${months[selectedMonth]} ${selectedDay}, ${selectedYear}"
                            showDatePicker = false
                        }
                    ) {
                        Text(
                            text = "Select",
                            color = primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text(
                            text = "Cancel",
                            color = onBackgroundColor.copy(alpha = 0.6f)
                        )
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Make your Task",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = onBackgroundColor
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigator.pop()
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.back_button),
                                contentDescription = "Back",
                                tint = onBackgroundColor
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                // Handle task creation logic here
                                navigator.pop()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Create Task",
                                tint = primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            }
        ) { innerPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(innerPaddings)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = onBackgroundColor.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.calendar),
                                contentDescription = "Calendar",
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = selectedDate,
                                fontSize = 16.sp,
                                color = onBackgroundColor,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            text = "Change",
                            fontSize = 14.sp,
                            color = primary,
                            fontWeight = FontWeight.Medium
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