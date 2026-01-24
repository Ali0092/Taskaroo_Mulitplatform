/**
 * Bottom navigation tab definitions for Taskaroo app.
 *
 * Defines the three main tabs: Home, Add Task, and Calendar.
 * Each tab implements Voyager's Tab interface for seamless navigation.
 *
 * @author Muhammad Ali
 * @date 2026-01-24
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.dev.taskaroo.screens.CalendarScreen
import com.dev.taskaroo.screens.CreateTaskScreen
import com.dev.taskaroo.screens.MainScreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.add_bottom_nav
import taskaroo.composeapp.generated.resources.calendar_bottom_nav
import taskaroo.composeapp.generated.resources.home_bottom_nav

/**
 * Sealed interface representing the three bottom navigation tabs.
 */
sealed class BottomNavTab(
    val title: String,
    val icon: DrawableResource
) : Tab {

    /**
     * Home tab - Shows the main task list screen
     */
    object HomeTab : BottomNavTab(
        title = "Home",
        icon = Res.drawable.home_bottom_nav
    ) {
        override val options: TabOptions
            @Composable
            get() {
                val icon = painterResource(Res.drawable.home_bottom_nav)
                return remember {
                    TabOptions(
                        index = 0u,
                        title = "Home",
                        icon = icon
                    )
                }
            }

        @Composable
        override fun Content() {
            Navigator(MainScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }

    /**
     * Add Task tab - Shows the create task screen
     */
    object AddTaskTab : BottomNavTab(
        title = "Add Task",
        icon = Res.drawable.add_bottom_nav
    ) {
        override val options: TabOptions
            @Composable
            get() {
                val icon = painterResource(Res.drawable.add_bottom_nav)
                return remember {
                    TabOptions(
                        index = 1u,
                        title = "Add Task",
                        icon = icon
                    )
                }
            }

        @Composable
        override fun Content() {
            Navigator(CreateTaskScreen(taskTimestampToEdit = null)) { navigator ->
                SlideTransition(navigator)
            }
        }
    }

    /**
     * Calendar tab - Shows the calendar/schedule screen
     */
    object CalendarTab : BottomNavTab(
        title = "Calendar",
        icon = Res.drawable.calendar_bottom_nav
    ) {
        override val options: TabOptions
            @Composable
            get() {
                val icon = painterResource(Res.drawable.calendar_bottom_nav)
                return remember {
                    TabOptions(
                        index = 2u,
                        title = "Calendar",
                        icon = icon
                    )
                }
            }

        @Composable
        override fun Content() {
            Navigator(CalendarScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}
