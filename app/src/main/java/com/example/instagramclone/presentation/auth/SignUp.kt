package com.example.instagramclone.presentation.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.instagramclone.utlis.SpacerHeight


@Composable
fun SignUp(navController: NavHostController, viewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var imageProfile by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val firebaseUser by viewModel.firebaseUser.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageProfile = it
        }
    }
            LaunchedEffect(firebaseUser) {
                if (firebaseUser != null) {
                    navController.navigate("mainScreen") {
                        popUpTo("signUp") { inclusive = true }
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
                Image(
                    painter = rememberAsyncImagePainter(imageProfile),
                    contentDescription = "",
                    modifier = Modifier.size(55.dp).clip(CircleShape).background(Color.Gray)
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
                SpacerHeight(10)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userName,
                    onValueChange = { userName = it },
                    placeholder = { Text("Username") }
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty() ||
                            fullName.isEmpty() || userName.isEmpty() || imageProfile == null
                        ) {
                            Toast.makeText(context, "please enter all details", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            viewModel.register(
                                email,
                                password,
                                fullName,
                                imageProfile!!,
                                userName
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 2.dp)
                    )
                    Text("or")
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 2.dp)
                    )
                }
                Text("SignUp with Facebook", color = Color.Blue.copy(.5f))
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    "Already have an account? Login",
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("login") })
                )
            }
        }

