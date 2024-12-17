package com.example.instagramclone.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.instagramclone.presentation.auth.AuthViewModel
import com.example.instagramclone.utlis.SpacerHeight
import com.example.instagramclone.utlis.SpacerWidth

@Composable
fun FollowScreen(innerPadding: PaddingValues, userId: String? = null, navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val allUser = authViewModel.allUser.collectAsState()

    LaunchedEffect(true) { authViewModel.getAllUsers() }
    var followerUserIdList = mutableListOf<String>()

    val user = allUser.value.firstOrNull { it.userId == userId }

    user?.followers?.forEach {
        followerUserIdList += it
    }
    val filterFollowerUser = allUser.value.filter {it.userId in followerUserIdList}

    Column( modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "",
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Text(text = "Follower")
        }

        LazyColumn {
            items(filterFollowerUser) { user ->
                Row(
                    modifier = Modifier, verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = user.profileImageUrl,
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                    SpacerWidth(4)
                    SpacerHeight(3)
                    Text(text = user.userName)
                }
            }
        }
    }
}