package com.example.instagramclone.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.instagramclone.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun Splash(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()){
        LaunchedEffect(true) {
                delay(2000)
            if (FirebaseAuth.getInstance().currentUser != null){
                navController.navigate("mainScreen") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            }else {
                navController.navigate("login") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.instagram),
            contentDescription = "",
            modifier = Modifier.size(80.dp).align(Alignment.Center),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Instagram", color = Color.Red)
            Text("From Meta")
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
