package com.example.instagramclone.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ProfileMore(innerPaddingValues: PaddingValues, navController: NavHostController, navController1: NavHostController) {
    Column(
        modifier = Modifier.padding(innerPaddingValues)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "",
            modifier = Modifier.clickable { navController1.popBackStack() }
        )
        Text(text = "Profile more")
        val viewModel: AuthViewModel = hiltViewModel()
        val firebaseUser by viewModel.firebaseUser.observeAsState(null)
        LaunchedEffect(firebaseUser) {
            if (firebaseUser == null) {
                navController.navigate("login")
            }
        }
        Text(text = "Logout", modifier = Modifier.clickable{viewModel.signOut()})
    }
}