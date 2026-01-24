/**
 * Root navigation screen with smooth tab transitions.
 *
 * Professional navigation container with animated transitions between tabs.
 *
 * @author Muhammad Ali
 * @date 2026-01-24
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.dev.taskaroo.common.TaskarooBottomNavBar
import com.dev.taskaroo.navigation.BottomNavTab

class RootNavigationScreen : Screen {

    @Composable
    override fun Content() {
        var previousTabIndex by remember { mutableStateOf(0) }
        var showBottomBar by remember { mutableStateOf(true) }

        TabNavigator(BottomNavTab.HomeTab) {
            val tabNavigator = LocalTabNavigator.current
            val tabs = listOf(
                BottomNavTab.HomeTab,
                BottomNavTab.AddTaskTab,
                BottomNavTab.CalendarTab
            )
            val currentTabIndex = tabs.indexOfFirst { it.key == tabNavigator.current.key }.coerceAtLeast(0)

            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    // Animated bottom bar visibility
                    AnimatedVisibility(
                        visible = showBottomBar,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(durationMillis = 250)
                        ) + fadeOut(animationSpec = tween(durationMillis = 250))
                    ) {
                        TaskarooBottomNavBar(
                            currentTab = tabNavigator.current,
                            onTabSelected = { tab ->
                                tabNavigator.current = tab
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                ) {
                    // Smooth animated transitions between tabs
                    AnimatedContent(
                        targetState = tabNavigator.current,
                        transitionSpec = {
                            val direction = if (currentTabIndex > previousTabIndex) 1 else -1

                            slideInHorizontally(
                                initialOffsetX = { it * direction },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(durationMillis = 400)
                            ) togetherWith slideOutHorizontally(
                                targetOffsetX = { -it * direction },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeOut(
                                animationSpec = tween(durationMillis = 400)
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { currentTab ->
                        previousTabIndex = currentTabIndex

                        Box(modifier = Modifier.fillMaxSize()) {
                            // Track navigation depth to hide/show bottom bar
                            TabContentWithBottomBarControl(
                                tab = currentTab,
                                onBottomBarVisibilityChange = { visible ->
                                    showBottomBar = visible
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
private fun TabContentWithBottomBarControl(
    tab: Tab,
    onBottomBarVisibilityChange: (Boolean) -> Unit
) {
    tab.Content()

    // Monitor the navigator inside each tab to control bottom bar visibility
    val navigator = LocalNavigator.current

    LaunchedEffect(navigator?.size) {
        // Show bottom bar only when at root of tab (navigator size == 1)
        val isAtRoot = navigator?.size == 1
        onBottomBarVisibilityChange(isAtRoot)
    }
}
