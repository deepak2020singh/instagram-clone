package com.example.instagramclone.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun Login(navController: NavHostController, viewModel: AuthViewModel) {
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var context = LocalContext.current
    val uiState by viewModel.firebaseUser.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

            LaunchedEffect(uiState) {
                if (uiState != null) {
                    navController.navigate("mainScreen") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Instagram", fontSize = 23.sp)
                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    placeholder = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = { Text("Password") }
                )

                Text(
                    "Forget Password?",
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("forgetPassword") })
                        .fillMaxWidth(),
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(10.dp))


                OutlinedButton(
                    onClick = {
                        if (email.value.isEmpty() || password.value.isEmpty()) {
                            Toast.makeText(context, "Please enter all details", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.login(email.value, password.value)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp))
                    Text("or")
                    HorizontalDivider(modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp))
                }
                Text("Login with Facebook", color = Color.Blue.copy(.5f))

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Don't have an account?",
                    modifier = Modifier.clickable { navController.navigate("signUp") }
                )
            }
        }




