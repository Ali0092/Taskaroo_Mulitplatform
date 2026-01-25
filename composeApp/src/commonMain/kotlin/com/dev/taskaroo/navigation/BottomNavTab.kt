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
import androidx.compose.runtime.LaunchedEffect
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
     * Content wrapper that tracks Navigator depth for bottom bar visibility control.
     * This must be called instead of Content() to properly track navigation state.
     */
    @Composable
    fun ContentWithBottomBarTracking(onBottomBarVisibilityChange: (Boolean) -> Unit) {
        when (this) {
            is HomeTab -> {
                Navigator(MainScreen()) { navigator ->
                    // Track navigator size from INSIDE the Navigator scope
                    LaunchedEffect(navigator.size) {
                        val isAtRoot = navigator.size == 1
                        onBottomBarVisibilityChange(isAtRoot)
                    }
                    SlideTransition(navigator)
                }
            }
            is CalendarTab -> {
                Navigator(CalendarScreen()) { navigator ->
                    LaunchedEffect(navigator.size) {
                        val isAtRoot = navigator.size == 1
                        onBottomBarVisibilityChange(isAtRoot)
                    }
                    SlideTransition(navigator)
                }
            }
        }
    }

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
                        index = 1u,
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
