package com.dev.taskaroo.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.common.TopAppBar
import com.dev.taskaroo.onBackgroundColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.info_icon
import taskaroo.composeapp.generated.resources.settings_icon
import taskaroo.composeapp.generated.resources.theme_icon

class SettingsScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold { innerPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(innerPaddings)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {

                TopAppBar(
                    title = "Set Settings ",
                    canShowNavigationIcon = true,
                    onBackButtonClick = {
                        navigator.pop()
                    }
                )

                Spacer(Modifier.height(24.dp))

                settingsScreenSingleItem(
                    modifier = Modifier,
                    title = "Theme",
                    icon = Res.drawable.theme_icon,
                    onItemClick = {

                    }
                )

                Spacer(Modifier.height(16.dp))

                settingsScreenSingleItem(
                    modifier = Modifier,
                    title = "About",
                    icon = Res.drawable.info_icon,
                    onItemClick = {

                    }
                )

            }
        }
    }
}


@Composable
fun settingsScreenSingleItem(modifier: Modifier, title: String, icon: DrawableResource, onItemClick: () -> Unit) {

    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).clickable {
            onItemClick()
        },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color = onBackgroundColor.copy(0.25f))
    ) {
        Row(modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null
            )
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = onBackgroundColor
            )
        }
    }

}