package com.dev.taskaroo.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.modal.PrefsModel
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.onPrimary
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.selectedItemColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.education
import taskaroo.composeapp.generated.resources.user
import taskaroo.composeapp.generated.resources.working

class PreferencesScreen: Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val prefsList = listOf(
            PrefsModel(Res.drawable.user, "Personal"),
            PrefsModel(Res.drawable.working,"Work"),
            PrefsModel(Res.drawable.education,"Education")
        )

        Scaffold { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().background(backgroundColor).padding(paddingValues).padding(horizontal = 16.dp)) {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "How do you plan to user Taskaroo?",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onBackgroundColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = "Choose what applies.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    repeat(prefsList.size) { index->
                        PreferenceSingleItem(prefsList[index].icon,prefsList[index].title) { selected->
                            // here comes the related action.
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF798F79)),
                    onClick = {
                        navigator.push(BasicInfoScreen())
                    }
                ) {
                    Text(text = "Proceed!", color = onPrimary, modifier = Modifier.padding(vertical = 3.dp))

                }

            }
        }

    }

}

@Composable
fun PreferenceSingleItem(icon: DrawableResource, title: String, onSelected: (String) -> Unit ) {

    var isChecked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                isChecked = !isChecked
                onSelected(title)
            }
            .background(Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if(isChecked) selectedItemColor else Color.Transparent),
        border = BorderStroke(width = 0.5.dp, color = onBackgroundColor),
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 21.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {

            Image(painter = painterResource(icon),contentDescription = null, modifier = Modifier.size(50.dp))

            Spacer(Modifier.width(8.dp))

            Text(text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = onBackgroundColor
            )
            Spacer(Modifier.weight(1f))

            Icon(imageVector = if (isChecked) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = primaryColorVariant
            )
        }

    }
}
