package com.dev.taskaroo.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.taskaroo.highPriorityBackground
import com.dev.taskaroo.highPriorityColor
import com.dev.taskaroo.lowPriorityBackground
import com.dev.taskaroo.lowPriorityColor
import com.dev.taskaroo.mediumPriorityBackground
import com.dev.taskaroo.mediumPriorityColor
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.primaryLiteColorVariant
import com.dev.taskaroo.urgentPriorityBackground
import com.dev.taskaroo.urgentPriorityColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.back_button
import taskaroo.composeapp.generated.resources.calendar

@Composable
fun TopAppBar(
    title: String,
    canShowNavigationIcon: Boolean,
    otherIcon: DrawableResource? = null,
    secondIcon: DrawableResource? = null,
    onBackButtonClick: () -> Unit = {},
    onOtherIconClick: () -> Unit = {},
    onSecondIconClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // CanShowNavigationIcon
        if (canShowNavigationIcon) {
            IconButton(
                onClick = {
                    onBackButtonClick()
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.back_button),
                    contentDescription = "Back",
                    tint = onBackgroundColor
                )
            }
        }

        // Main title
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Second icon (delete button)
        if (secondIcon != null) {
            Spacer(modifier = Modifier.width(12.dp))

            IconSurface(
                icon = secondIcon,
                getAddButtonClick = {
                    onSecondIconClick()
                }
            )
        }

        // Other icon
        if (otherIcon != null) {
            Spacer(modifier = Modifier.width(12.dp))

            IconSurface(
                icon = otherIcon,
                getAddButtonClick = {
                    onOtherIconClick()
                }
            )
        }
    }

}

@Composable
fun DeleteConfirmationDialog(
    showDialog: Boolean,
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Delete Task",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"$taskTitle\" permanently?",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm
                ) {
                    Text(
                        text = "Delete",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

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
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            Icon(
                modifier = Modifier
                    .size(20.dp),
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
fun TaskCardConcise(
    modifier: Modifier,
    taskData: TaskData,
    onTaskItemToggle: (String, Boolean) -> Unit = { _, _ -> },
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.combinedClickable(
            onClick = { onClick() },
            onLongClick = { onLongClick() }
        ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Title and Subtitle
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = taskData.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = onBackgroundColor
            )

            // Task Details Section
            if (taskData.taskList.isNotEmpty()) {
                // Task Items
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    taskData.taskList.forEach { taskItem ->
                        TaskItemRow(
                            taskItem = taskItem,
                            isConciseItem = true,
                            onToggle = { isChecked ->
                                onTaskItemToggle(taskItem.id, isChecked)
                            }
                        )
                    }
                }
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Deadline: "+taskData.deadline,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = primaryColorVariant
            )

        }
    }
}


@Composable
fun TaskCard(
    taskData: TaskData,
    onTaskItemToggle: (String, Boolean) -> Unit = { _, _ -> },
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .then(if (taskData.taskList.isNotEmpty()) Modifier.padding(top = 16.dp) else Modifier.padding(vertical = 16.dp))
        ) {
            // Title and Subtitle
            Text(
                modifier = Modifier
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
            )
            {
                // Priority Chip
                val (priorityColor, priorityBackground) = when (taskData.category.lowercase()) {
                    "urgent" -> urgentPriorityColor to urgentPriorityBackground
                    "high" -> highPriorityColor to highPriorityBackground
                    "medium" -> mediumPriorityColor to mediumPriorityBackground
                    "low" -> lowPriorityColor to lowPriorityBackground
                    else -> Color.Gray to Color.Transparent
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
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(8.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = priorityColor)
                        ) {}
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
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                ) {
                    Text(
                        text = "Task Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onBackgroundColor
                    )

                    // Progress Indicator
                    LinearProgressIndicator(
                        progress = { taskData.progress },
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
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
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
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "checkbox_scale"
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .drawBehind {
                val strokeWidth = Stroke(width = 2.dp.toPx())
                val radius = size.minDimension / 2

                // Draw circle border
                drawCircle(
                    color = if (checked) primaryColorVariant else Color.Gray.copy(alpha = 0.5f),
                    radius = radius,
                    style = if (checked) Stroke(width = strokeWidth.width) else strokeWidth
                )

                // Draw filled circle if checked
                if (checked) {
                    drawCircle(
                        color = primaryColorVariant,
                        radius = radius - strokeWidth.width / 2
                    )
                }
            }
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = fadeIn(animationSpec = tween(150)) + expandVertically(
                animationSpec = tween(150),
                expandFrom = Alignment.CenterVertically
            ),
            exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(
                animationSpec = tween(150),
                shrinkTowards = Alignment.CenterVertically
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun CapsuleFloatingActionButton(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(BorderStroke(width = 1.dp, color = primaryColorVariant), shape = CircleShape)
                .clickable {
                    onAddClick()
                }
                .background(onBackgroundColor.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.calendar),
                contentDescription = "add_task",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TaskItemRow(
    taskItem: TaskItem,
    isConciseItem: Boolean = false,
    onToggle: (Boolean) -> Unit
) {
    var isChecked by remember { mutableStateOf(taskItem.isCompleted) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularCheckbox(
            checked = isChecked,
            onCheckedChange = { checked ->
                isChecked = checked
                onToggle(checked)
            },
            modifier = Modifier.size(if (isConciseItem) 13.dp else 20.dp)
        )

        Text(
            text = taskItem.text,
            fontSize = if (isConciseItem) 13.sp else 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isChecked) onBackgroundColor.copy(alpha = 0.5f) else onBackgroundColor,
            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}