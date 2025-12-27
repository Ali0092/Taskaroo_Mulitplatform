package com.dev.taskaroo.screens

import HorizontalCalendar
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.common.TaskCardConcise
import com.dev.taskaroo.common.TopAppBar
import com.dev.taskaroo.database.LocalDatabase
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.primaryLiteColorVariant
import com.dev.taskaroo.utils.DateTimeUtils
import com.dev.taskaroo.utils.todayDate
import kotlinx.datetime.LocalDate
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.add_icon

val hoursList = listOf(
    "12:00\nAM",  // Midnight (hour 0)
    "01:00\nAM",  // hour 1
    "02:00\nAM",  // hour 2
    "03:00\nAM",  // hour 3
    "04:00\nAM",  // hour 4
    "05:00\nAM",  // hour 5
    "06:00\nAM",  // hour 6
    "07:00\nAM",  // hour 7
    "08:00\nAM",  // hour 8
    "09:00\nAM",  // hour 9
    "10:00\nAM",  // hour 10
    "11:00\nAM",  // hour 11
    "12:00\nPM",  // Noon (hour 12)
    "01:00\nPM",  // hour 13
    "02:00\nPM",  // hour 14
    "03:00\nPM",  // hour 15
    "04:00\nPM",  // hour 16
    "05:00\nPM",  // hour 17
    "06:00\nPM",  // hour 18
    "07:00\nPM",  // hour 19
    "08:00\nPM",  // hour 20
    "09:00\nPM",  // hour 21
    "10:00\nPM",  // hour 22
    "11:00\nPM",  // hour 23
)

class CalendarScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val databaseHelper = LocalDatabase.current

        // Selected date state
        var selectedDate by remember {
            mutableStateOf(todayDate())
        }

        // Tasks for selected date
        var tasksForSelectedDate by remember { mutableStateOf<List<TaskData>>(emptyList()) }

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
                    .background(backgroundColor)
                    .padding(innerPaddings)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                TopAppBar(
                    title = "Schedule",
                    canShowNavigationIcon = true,
                    otherIcon = Res.drawable.add_icon,
                    onBackButtonClick = {
                        navigator.pop()
                    },
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
                        color = onBackgroundColor
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
                            items = tasksForThisHour
                        )
                    }
                }

            }
        }
    }

}

@Composable
fun HourColumnItem(
    hour: String,
    items: List<TaskData>,
    modifier: Modifier = Modifier
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
                color = onBackgroundColor,
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
                        taskData = taskData
                    )
                }
            }

    }


}
}
