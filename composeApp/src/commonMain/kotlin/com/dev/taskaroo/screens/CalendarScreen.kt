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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.dev.taskaroo.modal.TaskData
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.primaryLiteColorVariant
import com.dev.taskaroo.utils.Utils.sampleTasks
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.add_icon

val hoursList = listOf(
    "01:00\nAM",
    "02:00\nAM",
    "03:00\nAM",
    "04:00\nAM",
    "05:00\nAM",
    "06:00\nAM",
    "07:00\nAM",
    "08:00\nAM",
    "09:00\nAM",
    "10:00\nAM",
    "11:00\nAM",
    "12:00\nPM",
    "01:00\nPM",
    "02:00\nPM",
    "03:00\nPM",
    "04:00\nPM",
    "05:00\nPM",
    "06:00\nPM",
    "07:00\nPM",
    "08:00\nPM",
    "09:00\nPM",
    "10:00\nPM",
    "11:00\nPM",
    "12:00\nPM",
)

class CalendarScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        Scaffold { innerPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(innerPaddings)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            )
            {
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

                HorizontalCalendar { selectedDate ->
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

                Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().verticalScroll(rememberScrollState())) {
                    repeat(hoursList.size) { hour ->
                        HourColumnItem(hour = hoursList[hour], items = sampleTasks)
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
            .padding(vertical = 8.dp)
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

                repeat(items.size) { index ->
                    TaskCardConcise(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        taskData = items[index]
                    )
                }
            }
        }




    }
}
