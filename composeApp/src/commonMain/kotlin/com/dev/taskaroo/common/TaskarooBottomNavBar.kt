/**
 * Professional animated bottom navigation bar for Taskaroo app.
 *
 * Features smooth animations, sliding indicator, proper spacing, and polished design.
 *
 * @author Muhammad Ali
 * @date 2026-01-24
 * @see <a href="https://muhammadali0092.netlify.app/">Portfolio</a>
 */
package com.dev.taskaroo.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import com.dev.taskaroo.navigation.BottomNavTab
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskarooBottomNavBar(
    currentTab: Tab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        BottomNavTab.HomeTab,
        BottomNavTab.AddTaskTab,
        BottomNavTab.CalendarTab
    )

    val selectedIndex = tabs.indexOfFirst { it.key == currentTab.key }.coerceAtLeast(0)

    // Floating animation
    val elevation by animateDpAsState(
        targetValue = 16.dp,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Main navigation container with floating effect
        Box(
            modifier = Modifier
                .height(68.dp)
                .fillMaxWidth(0.88f)
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(34.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                )
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 6.dp)
                ,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    BottomNavItem(
                        tab = tab,
                        isSelected = index == selectedIndex,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f)
                    )

                    // Add subtle dividers between items (except after last)
                    if (index < tabs.size - 1) {
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: BottomNavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Smooth color transitions
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        },
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        )
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        )
    )


    Column(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon container with animations
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.scale(iconScale)
        ) {
            Icon(
                painter = painterResource(tab.icon),
                contentDescription = tab.title,
                modifier = Modifier.size(32.dp),
                tint = iconColor
            )
        }
    }
}
