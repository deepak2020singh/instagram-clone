package com.example.instagramclone.presentation.main

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instagramclone.presentation.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EditProfile(innerPadding: PaddingValues, viewModel: AuthViewModel = hiltViewModel()) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var userName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var context = LocalContext.current

    val updatedSuccess by viewModel.updatedSuccess.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")

    LaunchedEffect(updatedSuccess) {
        if (updatedSuccess) {
            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit Profile")

        // Bio Input Field
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            placeholder = { Text("bio") }
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Save Button
        OutlinedButton(
            onClick = {
                if (userId != null) {
                    viewModel.updateProfile(bio, userId)
                }
            }
        ) {
            Text("Save")
        }
    }
}
