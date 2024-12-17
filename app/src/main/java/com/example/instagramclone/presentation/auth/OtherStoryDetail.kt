package com.example.instagramclone.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instagramclone.presentation.main.StoryViewModel


@Composable
fun OtherStoryDetail(innerPadding: PaddingValues, userId: String) {
    val storyViewModel: StoryViewModel = hiltViewModel()
    LaunchedEffect(true) { storyViewModel.getStories1() }
    val stories by storyViewModel.stories.collectAsState()
    val errorMessage by storyViewModel.errorMessage.collectAsState()


    val userStories = stories.filter { it.userId == userId }
    // Extract image URLs or content (you can adjust this based on your Story data model)
    val imageList: List<String> = userStories.flatMap { it.stories ?: emptyList() }

    val authViewModel: AuthViewModel = hiltViewModel()
    LaunchedEffect(true) { authViewModel.getAllUsers()}
    val allUser by authViewModel.allUser.collectAsState()

    val user = allUser.firstOrNull { it.userId == userId }
    val userName = user?.userName ?: "Unknown User"
    val userImage = user?.profileImageUrl ?: ""


    Column(
            modifier = Modifier.padding(innerPadding)
        ) {
       Stories(imageList, userName, userImage)
        }

    }


