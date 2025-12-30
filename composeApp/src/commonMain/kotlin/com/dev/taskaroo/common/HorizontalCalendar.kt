/**
 * Horizontal scrolling calendar widget component
 *
 * @author Muhammad Ali
 * @date 2025-12-30
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.primary
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.primaryLiteColorVariant
import com.dev.taskaroo.utils.todayDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.left_arrow
import taskaroo.composeapp.generated.resources.right_arrow


/**
 * Main horizontal calendar component displaying a week view with date selection
 *
 * @param modifier Modifier to apply to the calendar container
 * @param shape Shape for the date item containers
 * @param onDateClickListener Callback invoked when a date is selected
 */
@Composable
fun HorizontalCalendar(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(15.dp),
    onDateClickListener: (LocalDate) -> Unit
) {
    val dataSource = remember { CalendarDataSource() }
    var data by remember {
        mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today))
    }

    Column(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {

        Header(
            data = data,
            onPrevClickListener = { startDate ->
                data = dataSource.getData(
                    startDate = startDate.minus(7, DateTimeUnit.DAY),
                    lastSelectedDate = data.selectedDate.date
                )
            },
            onNextClickListener = { endDate ->
                data = dataSource.getData(
                    startDate = endDate.plus(1, DateTimeUnit.DAY),
                    lastSelectedDate = data.selectedDate.date
                )
            }
        )

        Content(
            data = data,
            shape = shape,
        ) { date ->
            data = data.copy(
                selectedDate = date,
                visibleDates = data.visibleDates.map {
                    it.copy(isSelected = it.date == date.date)
                }
            )
            onDateClickListener(date.date)
        }
    }
}

/**
 * Calendar header displaying current month/year and navigation arrows
 *
 * @param data The calendar UI model containing date range information
 * @param onPrevClickListener Callback invoked when previous arrow is clicked
 * @param onNextClickListener Callback invoked when next arrow is clicked
 */
@Composable
fun Header(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = "${data.startDate.date.month.name.lowercase().replaceFirstChar { it.uppercase() }}, ${data.startDate.date.year}",
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontSize = 19.sp,
                fontWeight = FontWeight.Normal,
                color = onBackgroundColor
            )
        )

        Icon(
            painter = painterResource(Res.drawable.left_arrow),
            contentDescription = null,
            modifier = Modifier.size(20.dp).clip(CircleShape).clickable {
                onPrevClickListener(data.startDate.date)
            }
        )

        Spacer(Modifier.width(12.dp))

        Icon(
            painter = painterResource(Res.drawable.right_arrow),
            contentDescription = null,
            modifier = Modifier.size(20.dp).clip(CircleShape).clickable {
                onNextClickListener(data.endDate.date)
            }
        )
    }
}

/**
 * Calendar content displaying the row of date items
 *
 * @param data The calendar UI model containing visible dates
 * @param shape Shape for the date item containers
 * @param onDateClickListener Callback invoked when a date is clicked
 */
@Composable
fun Content(
    data: CalendarUiModel,
    shape: Shape,
    onDateClickListener: (CalendarUiModel.Date) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        data.visibleDates.forEach {
            ContentItem(
                date = it,
                shape = shape,
                onClickListener = onDateClickListener
            )
        }
    }
}

/**
 * Individual date item in the calendar displaying day name and date number
 *
 * @param date The date data to display
 * @param shape Shape for the date item container
 * @param onClickListener Callback invoked when the date item is clicked
 */
@Composable
fun RowScope.ContentItem(
    date: CalendarUiModel.Date,
    shape: Shape,
    onClickListener: (CalendarUiModel.Date) -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp)
            .background(backgroundColor,shape)
            .clip(shape)
            .border(
                if (date.isSelected) 1.5.dp else 0.5.dp,
                if (date.isSelected) primary else primaryLiteColorVariant,
                shape
            )
            .clickable { onClickListener(date) }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = date.day, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, color = primaryColorVariant))
        Spacer(Modifier.height(8.dp))
        Text(text = date.date.dayOfMonth.toString(), style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = onBackgroundColor))
    }
}

/**
 * UI model representing the calendar state and visible dates
 *
 * @property selectedDate The currently selected date
 * @property visibleDates List of dates currently visible in the calendar
 */
data class CalendarUiModel(
    val selectedDate: Date,
    val visibleDates: List<Date>
) {
    val startDate = visibleDates.first()
    val endDate = visibleDates.last()

    /**
     * Represents a single date in the calendar
     *
     * @property date The LocalDate instance
     * @property isSelected Whether this date is currently selected
     * @property isToday Whether this date is today
     */
    data class Date(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean
    ) {
        val day: String = date.dayOfWeek.name.take(3)
    }
}

/**
 * Data source for generating calendar UI models
 */
class CalendarDataSource {

    /**
     * Returns today's date
     */
    val today: LocalDate
        get() = todayDate()

    /**
     * Generates calendar data for a week starting from the specified date
     *
     * @param startDate The first date to display in the calendar
     * @param lastSelectedDate The date that should be marked as selected
     * @return CalendarUiModel containing the week's dates and selection state
     */
    fun getData(
        startDate: LocalDate = today,
        lastSelectedDate: LocalDate
    ): CalendarUiModel {

        val visibleDates = (0..6).map {
            startDate.plus(it, DateTimeUnit.DAY)
        }

        return CalendarUiModel(
            selectedDate = toItem(lastSelectedDate, true),
            visibleDates = visibleDates.map {
                toItem(it, it == lastSelectedDate)
            }
        )
    }

    /**
     * Converts a LocalDate to a CalendarUiModel.Date
     *
     * @param date The date to convert
     * @param isSelected Whether this date should be marked as selected
     * @return CalendarUiModel.Date with appropriate flags set
     */
    private fun toItem(date: LocalDate, isSelected: Boolean) =
        CalendarUiModel.Date(
            date = date,
            isSelected = isSelected,
            isToday = date == today
        )
}
