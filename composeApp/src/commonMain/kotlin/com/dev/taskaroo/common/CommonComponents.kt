package com.dev.taskaroo.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.primaryLiteColorVariant
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun IconSurface(icon: DrawableResource, getAddButtonClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                getAddButtonClick()
            },
        color = Color.Transparent,
        shape = CircleShape,
        border = BorderStroke(1.dp, color = primaryLiteColorVariant)
    ) {
        Box(modifier = Modifier.size(45.dp), contentAlignment = Alignment.Center) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(icon),
                contentDescription = null,
                tint = onBackgroundColor
            )
        }
    }
}

@Composable
fun DotIndicator(
    pageCount: Int, currentPage: Int,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (index == currentPage) primaryColorVariant else Color.Gray.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
fun TaskChipRow(
    categories: List<String> = listOf("Work", "Personal", "Shopping", "Health"),
    onCategorySelected: (String) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(
                color = primaryLiteColorVariant.copy(alpha = 0.15f),
                shape = CircleShape
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .background(
                        color = if (isSelected) Color.White else Color.Transparent,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .then(if (isSelected) Modifier.border(width = 1.dp, primaryLiteColorVariant.copy(alpha = 0.5f), shape = CircleShape) else Modifier)
                    .clickable {
                        selectedCategory = category
                        onCategorySelected(category)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.Black else onBackgroundColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    taskData: TaskData,
    onTaskItemToggle: (String, Boolean) -> Unit = { _, _ -> }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            // Title and Subtitle
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
                text = taskData.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = onBackgroundColor
            )

            Text(
                text = taskData.subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = onBackgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp),
            )

            // Category and Deadline Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
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
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(modifier = Modifier.size(8.dp), shape = CircleShape, colors = CardDefaults.cardColors(containerColor = Color.LightGray)) {}
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = taskData.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = onBackgroundColor
                        )
                    }
                }

                // Deadline Chip
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
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Deadline",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = onBackgroundColor.copy(alpha = 0.7f)
                        )
                        Text(
                            text = taskData.deadline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = onBackgroundColor
                        )
                    }
                }
            }

            // Task Details Section
            if (taskData.taskList.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(
                            color = primaryLiteColorVariant.copy(0.5f)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Task Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onBackgroundColor
                    )

                    // Progress Indicator
                    LinearProgressIndicator(
                        progress = taskData.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = primaryColorVariant,
                        trackColor = primaryLiteColorVariant.copy(alpha = 0.3f)
                    )

                    Text(
                        text = taskData.progressText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = onBackgroundColor.copy(alpha = 0.7f)
                    )

                    // Task Items
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        taskData.taskList.forEach { taskItem ->
                            TaskItemRow(
                                taskItem = taskItem,
                                onToggle = { isChecked ->
                                    onTaskItemToggle(taskItem.id, isChecked)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemRow(
    taskItem: TaskItem,
    onToggle: (Boolean) -> Unit
) {
    var isChecked by remember { mutableStateOf(taskItem.isCompleted) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { checked ->
                isChecked = checked
                onToggle(checked)
            },
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = taskItem.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = if (isChecked) onBackgroundColor.copy(alpha = 0.5f) else onBackgroundColor,
            modifier = Modifier.weight(1f)
        )
    }
}