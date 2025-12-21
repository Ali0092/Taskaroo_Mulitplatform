package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.common.CapsuleFloatingActionButton
import com.dev.taskaroo.common.TaskCard
import com.dev.taskaroo.common.TaskChipRow
import com.dev.taskaroo.common.TopAppBar
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import com.dev.taskaroo.utils.Utils.sampleTasks
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.menu_icon

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

        // State for selected category
        var selectedCategory by remember { mutableStateOf("Work") }
        val navigator = LocalNavigator.currentOrThrow

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
                    title = titles.random(),
                    canShowNavigationIcon = false,
                    otherIcon = Res.drawable.menu_icon,
                    onOtherIconClick = {
                        // later this will be added...
                    }
                )

                Spacer(Modifier.height(16.dp))

//                TaskChipRow(
//                    onCategorySelected = { category ->
//                        selectedCategory = category
//                    }
//                )

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
//                                val taskIndex = sampleTasks.indexOfFirst { it.id == task.id }
//                                if (taskIndex != -1) {
//                                    val currentTask = sampleTasks[taskIndex]
//                                    val updatedTaskList = currentTask.taskList.map { item ->
//                                        if (item.id == taskId) {
//                                            item.copy(isCompleted = isChecked)
//                                        } else {
//                                            item
//                                        }
//                                    }
//                                    val completedCount = updatedTaskList.count { it.isCompleted }
//                                    sampleTasks[taskIndex] = currentTask.copy(
//                                        taskList = updatedTaskList,
//                                        completedTasks = completedCount
//                                    )
//                                }
                            }
                        )
                    }
                }
            }
        }
    }

}