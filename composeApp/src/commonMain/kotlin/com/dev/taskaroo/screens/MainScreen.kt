package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.common.IconSurface
import com.dev.taskaroo.common.TaskCard
import com.dev.taskaroo.common.TaskChipRow
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.menu_icon
import taskaroo.composeapp.generated.resources.search_icon

class MainScreen : Screen {

    @Composable
    override fun Content() {
        // Sample task data
        val sampleTasks = remember {
            mutableStateListOf(
                TaskData(
                    id = "1",
                    title = "Complete Project Proposal",
                    subtitle = "Finalize and submit the Q4 project proposal to the client",
                    category = "high",
                    deadline = "Dec 15, 2024",
                    taskList = listOf(
                        TaskItem("1", "Review project requirements"),
                        TaskItem("2", "Create budget breakdown"),
                        TaskItem("3", "Prepare presentation slides")
                    ),
                    completedTasks = 1
                ),
                TaskData(
                    id = "2",
                    title = "Team Meeting",
                    subtitle = "Weekly sync with the development team",
                    category = "medium",
                    deadline = "Dec 12, 2024",
                    taskList = listOf(
                        TaskItem("4", "Prepare agenda items"),
                        TaskItem("5", "Review last week's action items")
                    ),
                    completedTasks = 0
                ),
                TaskData(
                    id = "3",
                    title = "Code Review",
                    subtitle = "Review pull requests from team members",
                    category = "urgent",
                    deadline = "Dec 11, 2024",
                    taskList = listOf(
                        TaskItem("6", "Review authentication module"),
                        TaskItem("7", "Test API integration")
                    ),
                    completedTasks = 0
                ),
                TaskData(
                    id = "4",
                    title = "Documentation Update",
                    subtitle = "Update API documentation for new endpoints",
                    category = "low",
                    deadline = "Dec 20, 2024",
                    taskList = listOf(
                        TaskItem("8", "Document new authentication flow"),
                        TaskItem("9", "Update endpoint examples"),
                        TaskItem("10", "Review with technical writer")
                    ),
                    completedTasks = 2
                )
            )
        }

        // State for selected category
        var selectedCategory by remember { mutableStateOf("Work") }

        Scaffold { innerPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(innerPaddings)
                    .padding(horizontal = 16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconSurface(
                        Res.drawable.menu_icon,
                        getAddButtonClick = {

                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
//                    IconSurface(
//                        Res.drawable.search_icon,
//                        getAddButtonClick = {
//
//                        }
//                    )
                }

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Jumpstart your morning &\nmake it productive.",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )

                TaskChipRow(
                    onCategorySelected = { category ->
                        selectedCategory = category
                    }
                )

                // Filter tasks based on selected category
                val filteredTasks = when (selectedCategory) {
                    "Work" -> sampleTasks.filter { it.category in listOf("high", "medium") }
                    "Personal" -> sampleTasks.filter { it.category == "low" }
                    "Shopping" -> sampleTasks.filter { it.category == "medium" }
                    "Health" -> sampleTasks.filter { it.category in listOf("urgent", "high") }
                    else -> sampleTasks
                }

                // LazyColumn with task cards
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTasks) { task ->
                        TaskCard(
                            taskData = task,
                            onTaskItemToggle = { taskId, isChecked ->
                                // Find the task in the original list and update its completion status
                                val taskIndex = sampleTasks.indexOfFirst { it.id == task.id }
                                if (taskIndex != -1) {
                                    val currentTask = sampleTasks[taskIndex]
                                    val updatedTaskList = currentTask.taskList.map { item ->
                                        if (item.id == taskId) {
                                            item.copy(isCompleted = isChecked)
                                        } else {
                                            item
                                        }
                                    }
                                    val completedCount = updatedTaskList.count { it.isCompleted }
                                    sampleTasks[taskIndex] = currentTask.copy(
                                        taskList = updatedTaskList,
                                        completedTasks = completedCount
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

}