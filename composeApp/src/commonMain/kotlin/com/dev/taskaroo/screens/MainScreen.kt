package com.dev.taskaroo.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.common.CapsuleFloatingActionButton
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
        val sampleTasks = remember {
            mutableStateListOf(
                TaskData(
                    id = "1",
                    title = "Fix Critical Production Bug",
                    subtitle = "Server downtime issue needs immediate attention",
                    category = "Urgent",
                    deadline = "Today",
                    taskList = listOf(
                        TaskItem("1", "Identify root cause of server crash", isCompleted = true),
                        TaskItem("2", "Deploy hotfix to production"),
                        TaskItem("3", "Monitor system stability")
                    ),
                    completedTasks = 1
                ),
                TaskData(
                    id = "2",
                    title = "Complete Project Proposal",
                    subtitle = "Finalize and submit the Q4 project proposal to the client",
                    category = "High",
                    deadline = "Dec 15, 2024",
                    taskList = listOf(
                        TaskItem("4", "Review project requirements"),
                        TaskItem("5", "Create budget breakdown"),
                        TaskItem("6", "Prepare presentation slides")
                    ),
                    completedTasks = 0
                ),
                TaskData(
                    id = "3",
                    title = "Team Meeting Preparation",
                    subtitle = "Weekly sync with the development team",
                    category = "Medium",
                    deadline = "Dec 12, 2024",
                    taskList = listOf(
                        TaskItem("7", "Prepare agenda items", isCompleted = true),
                        TaskItem("8", "Review last week's action items")
                    ),
                    completedTasks = 1
                ),
                TaskData(
                    id = "4",
                    title = "Code Review",
                    subtitle = "Review pull requests from team members",
                    category = "High",
                    deadline = "Tomorrow",
                    taskList = listOf(
                        TaskItem("9", "Review authentication module"),
                        TaskItem("10", "Test API integration", isCompleted = true)
                    ),
                    completedTasks = 1
                ),
                TaskData(
                    id = "5",
                    title = "Documentation Update",
                    subtitle = "Update API documentation for new endpoints",
                    category = "Low",
                    deadline = "Dec 20, 2024",
                    taskList = listOf(
                        TaskItem("11", "Document new authentication flow"),
                        TaskItem("12", "Update endpoint examples"),
                        TaskItem("13", "Review with technical writer", isCompleted = true)
                    ),
                    completedTasks = 1
                )
            )
        }

        // State for selected category
        var selectedCategory by remember { mutableStateOf("Work") }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            floatingActionButton = {
                // Floating Action Button
                CapsuleFloatingActionButton(
                    onAddClick = {
                        navigator.push(CreateTaskScreen())
                    },
                )
            }
        ) { innerPaddings ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .padding(innerPaddings)
                        .padding(horizontal = 16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            modifier = Modifier.padding(top = 16.dp).weight(1f),
                            text = titles.random(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        IconSurface(
                            Res.drawable.menu_icon,
                            getAddButtonClick = {

                            }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    TaskChipRow(
                        onCategorySelected = { category ->
                            selectedCategory = category
                        }
                    )

                    // Filter tasks based on selected category
                    val filteredTasks = when (selectedCategory) {
                        "Work" -> sampleTasks.filter { it.category in listOf("High", "Medium") }
                        "Personal" -> sampleTasks.filter { it.category == "Low" }
                        "Shopping" -> sampleTasks.filter { it.category == "Medium" }
                        "Health" -> sampleTasks.filter { it.category in listOf("Urgent", "High") }
                        else -> sampleTasks
                    }

                    // LazyColumn with task cards
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
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

}