package com.dev.taskaroo.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.taskaroo.primaryColorVariant

//
//@Composable
//fun GradientButton(
//    text: String,
//    modifier: Modifier = Modifier,
//    gradientColors: List<Color> = listOf(gradientEndColor, gradientStartColor),
//    onClick: () -> Unit,
//) {
//    Box(
//        modifier = modifier
//            .clip(RoundedCornerShape(50.dp))
//            .background(brush = Brush.horizontalGradient(gradientColors))
//            .clickable(onClick = onClick)
//            .padding(vertical = 12.dp, horizontal = 24.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = text, color = textColor, modifier = Modifier.padding(vertical = 3.dp))
//    }
//}

@Composable
fun IconSurface(icon: ImageVector, getAddButtonClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable {
            getAddButtonClick()
        }, color = MaterialTheme.colorScheme.surfaceContainerHighest, shape = RoundedCornerShape(12.dp), shadowElevation = 1.dp
    ) {
        Icon(
            modifier = Modifier
                .padding(12.dp)
                .size(25.dp),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
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
fun NeumorphicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFECEAEA), // Light gray background
    lightShadowColor: Color = Color.White.copy(alpha = 0.8f),
    darkShadowColor: Color = Color.Black.copy(alpha = 0.15f),
    cornerRadius: Dp = 12.dp,
    elevationPressed: Dp = 2.dp,
    elevationReleased: Dp = 8.dp
) {
    var isPressed by remember { mutableStateOf(false) }
    val currentElevation by animateFloatAsState(
        targetValue = if (isPressed) elevationPressed.value else elevationReleased.value,
        animationSpec = spring(), label = "elevationAnimation"
    )

    Box(
        modifier = modifier
            .size(100.dp, 50.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            }
            .shadow(
                elevation = currentElevation.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = darkShadowColor,
                spotColor = darkShadowColor,
                clip = false // Allow shadows to extend beyond bounds
            )
            .shadow(
                elevation = currentElevation.dp / 2, // Lighter elevation for the highlight
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = lightShadowColor,
                spotColor = lightShadowColor,
                clip = false
            )
            .background(backgroundColor, RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.DarkGray)
    }
}