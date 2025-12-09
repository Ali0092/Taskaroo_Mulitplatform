package com.dev.taskaroo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.dev.taskaroo.backgroundColor

class BasicInfoScreen: Screen {
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
                    .padding(horizontal = 16.dp)
            ) {

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Screen subtitle",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = "Screen subtitle 2",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {

                    Text(
                        text = "Some text",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(0.5f)
                    )

                    Spacer(Modifier.height(12.dp))

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
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,   // Border color when focused
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground, // Border color when not focused
                            cursorColor = MaterialTheme.colorScheme.onBackground,          // Cursor color
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        )
                    )
                    Spacer(Modifier.height(12.dp))

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
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,   // Border color when focused
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground, // Border color when not focused
                            cursorColor = MaterialTheme.colorScheme.onBackground,          // Cursor color
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        )
                    )
                }

            }
        }
    }
}