package com.dev.taskaroo.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.dev.taskaroo.backgroundColor
import com.dev.taskaroo.onBackgroundColor
import com.dev.taskaroo.onPrimary
import com.dev.taskaroo.primaryColorVariant
import com.dev.taskaroo.primaryLiteColorVariant
import org.jetbrains.compose.resources.painterResource
import taskaroo.composeapp.generated.resources.Res
import taskaroo.composeapp.generated.resources.onboarding_3

class BasicInfoScreen : Screen {
    @Composable
    override fun Content() {
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Personal Info",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.size(150.dp),
                    color = primaryLiteColorVariant,
                    shape = CircleShape,
                    border = BorderStroke(width = 1.dp, primaryColorVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(Res.drawable.onboarding_3), contentDescription = null, contentScale = ContentScale.Crop)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "First Name",
                        fontSize = 16.sp,
                        color = primaryColorVariant,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                        },
                        label = { "Name" },
                        placeholder = { "Enter Name" },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColorVariant,
                            unfocusedBorderColor = primaryColorVariant,
                            cursorColor = primaryColorVariant,
                            focusedLabelColor = primaryColorVariant,
                            unfocusedLabelColor = primaryColorVariant,
                            focusedTextColor = onBackgroundColor,
                            unfocusedTextColor = primaryLiteColorVariant,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Nick Name (optional)",
                        fontSize = 15.sp,
                        color = primaryColorVariant,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                        },
                        label = { "Enter Father Name" },
                        placeholder = { "Father Nama" },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColorVariant,
                            unfocusedBorderColor = primaryColorVariant,
                            cursorColor = primaryColorVariant,
                            focusedLabelColor = primaryColorVariant,
                            unfocusedLabelColor = primaryColorVariant,
                            focusedTextColor = onBackgroundColor,
                            unfocusedTextColor = primaryLiteColorVariant,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        )
                    )

                    Spacer(Modifier.weight(1f))

                    Button(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF798F79)),
                        onClick = {
                            //..
                        }
                    ) {
                        Text(text = "Proceed!", color = onPrimary, modifier = Modifier.padding(vertical = 3.dp))

                    }

                }

            }
        }
    }
}