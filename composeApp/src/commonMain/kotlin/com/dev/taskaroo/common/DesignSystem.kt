/**
 * Reusable UI components library for Taskaroo application
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.common

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.taskaroo.completedStatusBackground
import com.dev.taskaroo.completedStatusColor
import com.dev.taskaroo.highPriorityBackground
import com.dev.taskaroo.highPriorityColor
import com.dev.taskaroo.lowPriorityBackground
import com.dev.taskaroo.lowPriorityColor
import com.dev.taskaroo.mediumPriorityBackground
import com.dev.taskaroo.mediumPriorityColor
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.modal.TaskItem
import com.dev.taskaroo.overdueStatusBackground
import com.dev.taskaroo.overdueStatusColor
import com.dev.taskaroo.primary
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.primaryLiteColorVariant
import com.dev.taskaroo.undoneStatusBackground
import com.dev.taskaroo.undoneStatusColor
import com.dev.taskaroo.urgentPriorityBackground
import com.dev.taskaroo.urgentPriorityColor
import com.dev.taskaroo.utils.DateTimeUtils.isTaskOverdue
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.back_button
import taskaroo.composeapp.generated.resources.calendar

/**
 * Custom top app bar component with navigation and action icons
 *
 * @param title The text to display as the app bar title
 * @param canShowNavigationIcon Whether to show the back navigation button
 * @param otherIcon Optional drawable resource for the primary action icon
 * @param trailingIcon Optional drawable resource for the secondary action icon
 * @param onBackButtonClick Callback invoked when back button is clicked
 * @param onTrailingIconClick Callback invoked when primary action icon is clicked
 * @param onOtherIconClick Callback invoked when secondary action icon is clicked
 */
@Composable
fun TaskarooTopAppBar(
    title: String,
    canShowNavigationIcon: Boolean,
    otherIcon: DrawableResource? = null,
    trailingIcon: DrawableResource? = null,
    onBackButtonClick: () -> Unit = {},
    onTrailingIconClick: () -> Unit = {},
    onOtherIconClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (canShowNavigationIcon) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onBackButtonClick()
                    },
                painter = painterResource(Res.drawable.back_button),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (otherIcon != null) {
            TaskarooRoundedIcon(
                icon = otherIcon,
                getAddButtonClick = {
                    onOtherIconClick()
                }
            )
        }

        if (trailingIcon != null) {
            TaskarooRoundedIcon(
                icon = trailingIcon,
                getAddButtonClick = {
                    onTrailingIconClick()
                }
            )
        }
    }

}

/**
 * Alert dialog for confirming task deletion
 *
 * @param showDialog Whether the dialog should be displayed
 * @param taskTitle The title of the task to be deleted
 * @param onDismiss Callback invoked when dialog is dismissed or cancelled
 * @param onConfirm Callback invoked when user confirms deletion
 */
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"$taskTitle\" permanently?",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
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
                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Circular icon button with transparent background and border
 *
 * @param icon The drawable resource for the icon
 * @param getAddButtonClick Callback invoked when the icon is clicked
 */
@Composable
fun TaskarooRoundedIcon(icon: DrawableResource, getAddButtonClick: () -> Unit) {
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
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * Horizontal row of selectable category chips with pill-shaped design
 * Scrollable horizontally when content exceeds available width
 *
 * @param categories List of category names to display
 * @param onCategorySelected Callback invoked when a category is selected
 */
@Composable
fun TaskChipRow(
    categories: List<String>,
    onCategorySelected: (String) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .background(
                color = primaryLiteColorVariant.copy(alpha = 0.15f),
                shape = CircleShape
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
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
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


/**
 * Compact task card displaying task title, items, and deadline
 *
 * @param modifier Modifier to apply to the card
 * @param taskData The task data to display
 * @param onTaskItemToggle Callback invoked when a task item checkbox is toggled
 * @param onClick Callback invoked when the card is clicked
 * @param onLongClick Callback invoked when the card is long-pressed
 */
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Show meeting link if exists, otherwise show description
            if (taskData.isMeeting && taskData.meetingLink.isNotEmpty()) {
                val uriHandler = LocalUriHandler.current

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            try {
                                uriHandler.openUri(taskData.meetingLink)
                            } catch (e: Exception) {
                                // Handle invalid URL gracefully
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Meeting link",
                        tint = Color(0xFF0066CC),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = taskData.meetingLink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF0066CC),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = TextDecoration.Underline
                    )
                }
            } else if (taskData.subtitle.isNotEmpty()) {
                // Subtitle
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = taskData.subtitle,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            // Task Details Section
            if (taskData.taskList.isNotEmpty()) {
                // Task Items
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    taskData.taskList.take(2).forEach { taskItem ->
                        TaskItemRow(
                            taskItem = taskItem,
                            maxLines = 2,
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
                text = "Due: " + taskData.deadline,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )

        }
    }
}


/**
 * Full-featured task card with title, subtitle, priority, deadline, and progress tracking
 *
 * @param taskData The task data to display
 * @param onTaskItemToggle Callback invoked when a task item checkbox is toggled
 * @param onClick Callback invoked when the card is clicked
 * @param onLongClick Callback invoked when the card is long-pressed
 */
@Composable
fun TaskCard(
    taskData: TaskData,
    onTaskDoneToggle: (Boolean) -> Unit = {},
    onTaskItemToggle: (String, Boolean) -> Unit = { _, _ -> },
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {

    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .alpha(if (taskData.isDone) 0.85f else 1f)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .then(if (taskData.taskList.isNotEmpty()) Modifier.padding(top = 16.dp) else Modifier.padding(vertical = 16.dp))
        ) {
            // Title, Subtitle and Done Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = taskData.title,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = if (taskData.isDone) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(Modifier.height(12.dp))

                TaskarooStatusBadge(
                    status = taskData.isDone.toTaskStatus(),
                    isOverdue = isTaskOverdue(taskData.timestampMillis),
                    onStatusChange = { newStatus ->
                        onTaskDoneToggle(newStatus.toBoolean())
                    },
                    fullWidth = false
                )
            }

            Spacer(Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 12.dp)) {

                // Show meeting link if exists, otherwise show description
                if (taskData.isMeeting && taskData.meetingLink.isNotEmpty()) {
                    // Show clickable meeting link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                try {
                                    uriHandler.openUri(taskData.meetingLink)
                                } catch (e: Exception) {
                                    // Handle invalid URL gracefully
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Meeting link",
                            tint = Color(0xFF0F7BE8),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = taskData.meetingLink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF0F7BE8),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                } else if (taskData.subtitle.isNotEmpty()) {
                    // Show description only if no meeting link
                    Text(
                        text = taskData.subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

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
                            color = MaterialTheme.colorScheme.onBackground
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
                            text = "Due",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            text = taskData.deadline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sub-tasks list",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = taskData.progressText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }

                    // Progress Indicator
                    LinearProgressIndicator(
                        progress = { taskData.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = primaryColorVariant,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    // Task Items
                    Column(
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        taskData.taskList.take(2).forEach { taskItem ->
                            TaskItemRow(
                                taskItem = taskItem,
                                maxLines = 2,
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

/**
 * Custom circular checkbox with animated check mark
 * @param checked Whether the checkbox is currently checked
 * @param onCheckedChange Callback invoked when checkbox state changes
 * @param modifier Modifier to apply to the checkbox
 */
@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    uncheckColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .drawBehind {
                val strokeWidth = Stroke(width = 1.dp.toPx())
                val radius = size.minDimension / 2

                // Draw circle border
                drawCircle(
                    color = uncheckColor.copy(alpha = if (checked) 0.5f else 0.8f),
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
            .clip(CircleShape)
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
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Task Status enum representing the completion status of a task
 *
 * @property displayName The user-facing display name for the status
 * @property isCompleted Whether this status represents a completed task
 */
enum class TaskStatus(val displayName: String, val isCompleted: Boolean) {
    /** Task is not yet completed */
    UNDONE("Undone", false),

    /** Task has been completed */
    COMPLETED("Done", true),

    /** Task is undone and past its deadline */
    OVERDUE("Overdue", false)
}

/**
 * Extension function to convert Boolean to TaskStatus
 */
fun Boolean.toTaskStatus() = if (this) TaskStatus.COMPLETED else TaskStatus.UNDONE

/**
 * Extension function to convert TaskStatus to Boolean
 */
fun TaskStatus.toBoolean() = this.isCompleted

/**
 * Task status badge with color-coded visual feedback and dropdown menu for status changes.
 * Displays task completion status with automatic overdue detection.
 *
 * Visual Design:
 * - Rounded corners (8dp)
 * - Colored background and text based on status
 * - Icon indicator (check circle for completed, radio button for undone)
 * - Optional dropdown arrow for full-width variant
 *
 * @param status Current task status (UNDONE, COMPLETED, or OVERDUE)
 * @param isOverdue Whether the task deadline has passed
 * @param onStatusChange Callback invoked when user changes the status
 * @param modifier Modifier to apply to the badge container
 * @param fullWidth If true, badge expands to full width and shows dropdown arrow
 */
@Composable
fun TaskarooStatusBadge(
    status: TaskStatus,
    isOverdue: Boolean,
    onStatusChange: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }

    // Determine display status and colors
    val displayStatus = if (!status.isCompleted && isOverdue) {
        TaskStatus.OVERDUE
    } else {
        status
    }

    val shape = if (fullWidth) RoundedCornerShape(12.dp) else CircleShape

    val statusColor = when (displayStatus) {
        TaskStatus.COMPLETED -> completedStatusColor
        TaskStatus.OVERDUE -> overdueStatusColor
        TaskStatus.UNDONE -> undoneStatusColor
    }

    Box(modifier = modifier) {
        // Status Badge
        Box(
            modifier = Modifier
                .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
                .background(
                    color = if (fullWidth) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
                    shape = shape
                )
                .clip(shape)
                .clickable { showDialog = true }
                .padding(horizontal = if (fullWidth) 16.dp else 8.dp, vertical = if (fullWidth) 12.dp else 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status indicator icon
                Icon(
                    imageVector = if (status.isCompleted) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(if (fullWidth) 24.dp else 16.dp)
                )

                // Status text
                Text(
                    text = displayStatus.displayName,
                    fontSize = if (fullWidth) 16.sp else 12.sp,
                    fontWeight = if (fullWidth) FontWeight.Bold else FontWeight.Medium,
                    color = statusColor
                )

                if (fullWidth) {
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Change status",
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Status Selection Dialog
        TaskStatusDialog(
            showDialog = showDialog,
            currentStatus = status,
            onStatusSelected = { newStatus ->
                onStatusChange(newStatus)
            },
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * Dialog for selecting task completion status.
 * Provides a visual selection interface matching the app's theme.
 * Shows all three status options: Undone, Done, and Overdue.
 * User selects a status and confirms with OK button.
 *
 * @param showDialog Whether to display the dialog
 * @param currentStatus Current task status
 * @param onStatusSelected Callback when OK is pressed with selected status
 * @param onDismiss Callback when dialog is dismissed or cancelled
 */
@Composable
fun TaskStatusDialog(
    showDialog: Boolean,
    currentStatus: TaskStatus,
    onStatusSelected: (TaskStatus) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        var selectedStatus by remember { mutableStateOf(currentStatus) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Change Status",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select task status:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    // Undone Status Option
                    StatusOptionCard(
                        icon = Icons.Default.RadioButtonUnchecked,
                        statusText = "Undone",
                        statusColor = undoneStatusColor,
                        statusBackground = undoneStatusBackground,
                        isSelected = selectedStatus == TaskStatus.UNDONE,
                        onClick = { selectedStatus = TaskStatus.UNDONE }
                    )

                    // Done Status Option
                    StatusOptionCard(
                        icon = Icons.Default.CheckCircle,
                        statusText = "Done",
                        statusColor = completedStatusColor,
                        statusBackground = completedStatusBackground,
                        isSelected = selectedStatus == TaskStatus.COMPLETED,
                        onClick = { selectedStatus = TaskStatus.COMPLETED }
                    )

                    // Overdue Status Option
                    StatusOptionCard(
                        icon = Icons.Default.RadioButtonUnchecked,
                        statusText = "Overdue",
                        statusColor = overdueStatusColor,
                        statusBackground = overdueStatusBackground,
                        isSelected = selectedStatus == TaskStatus.OVERDUE,
                        onClick = { selectedStatus = TaskStatus.OVERDUE }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onStatusSelected(selectedStatus)
                        onDismiss()
                    }
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, color = primary)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Individual status option card for TaskStatusDialog.
 * Shows a check mark when selected for visual feedback.
 *
 * @param icon The icon to display for this status
 * @param statusText The text label for this status
 * @param statusColor The color for text and icons
 * @param statusBackground The background color for the card
 * @param isSelected Whether this option is currently selected
 * @param onClick Callback when the card is clicked
 */
@Composable
private fun StatusOptionCard(
    icon: ImageVector,
    statusText: String,
    statusColor: Color,
    statusBackground: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) statusBackground else MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(
            width = 1.dp,
            color = statusColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = statusText,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = statusColor,
                modifier = Modifier.weight(1f)
            )

            // Show check mark when selected
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Pill-shaped floating action button with calendar icon and text
 *
 * @param onAddClick Callback invoked when the button is clicked
 * @param modifier Modifier to apply to the button container
 */
@Composable
fun CapsuleFloatingActionButton(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .border(BorderStroke(width = 1.dp, color = primaryColorVariant.copy(alpha = 0.8f)), shape = CircleShape)
                .clickable {
                    onAddClick()
                }
                .background(primary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Calendar", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Image(
                painter = painterResource(Res.drawable.calendar),
                contentDescription = "add_task",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Individual task item row with checkbox and text
 *
 * @param taskItem The task item data to display
 * @param isConciseItem Whether to use smaller sizing for compact layout
 * @param onToggle Callback invoked when the checkbox is toggled
 */
@Composable
fun TaskItemRow(
    modifier: Modifier = Modifier,
    taskItem: TaskItem,
    maxLines: Int,
    isConciseItem: Boolean = false,
    onToggle: (Boolean) -> Unit
) {
    var isChecked by remember { mutableStateOf(taskItem.isCompleted) }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularCheckbox(
            checked = isChecked,
            uncheckColor = MaterialTheme.colorScheme.onBackground,
            onCheckedChange = { checked ->
                isChecked = checked
                onToggle(checked)
            },
            modifier = Modifier.size(if (isConciseItem) 15.dp else 18.dp)
        )

        Text(
            text = taskItem.text,
            fontSize = if (isConciseItem) 13.sp else 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (isChecked) 0.5f else 0.8f),
            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}