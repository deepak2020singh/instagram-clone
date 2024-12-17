package com.example.instagramclone.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.instagramclone.presentation.auth.Routes
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Search(innerPadding: PaddingValues, navController1: NavHostController, searchViewModel: SearchViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers by searchViewModel.filteredUsers.collectAsState()
    var currentUserId by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        // Fetch current user ID after auth is ready
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        searchViewModel.getAllUsers(currentUserId)
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(5.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                searchViewModel.filterUsers(it)
            },
            placeholder = { Text(text = "Search") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier.clickable { }
                )
            },
            maxLines = 1,
            label = { Text(text = "Search") }
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(filteredUsers){user->
                // Directly check if currentUserId is in the 'following' list
                val isFollowing = currentUserId in user.followers
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.Gray.copy(.6f)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = user.profileImageUrl),
                        contentDescription = "",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable{ navController1.navigate("${Routes.Profile.route}/${user.userId}")},
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = user.userName)
                    Spacer(modifier = Modifier.weight(1f))

                    // Conditional text for Follow/Unfollow based on follow status
                    Text(
                        text = if (isFollowing) "Unfollow" else "Follow",
                        modifier = Modifier
                            .clickable {
                                if (isFollowing) {
                                    searchViewModel.unfollowUser(user.userId, currentUserId) // Unfollow user
                                } else {
                                    searchViewModel.followUser(user.userId, currentUserId) // Follow user
                                }
                            }
                            .background(color = Color.Gray)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}


