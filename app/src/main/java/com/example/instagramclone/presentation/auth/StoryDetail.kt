@file:Suppress("DEPRECATION")

package com.example.instagramclone.presentation.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.instagramclone.presentation.main.StoryViewModel
import com.example.instagramclone.utlis.SpacerWidth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun StoryDetail(innerPadding: PaddingValues, useId: String) {
    val authViewModel: AuthViewModel = hiltViewModel()
    LaunchedEffect(true) { authViewModel.getAllUsers() }

    val allUser by authViewModel.allUser.collectAsState()
    val user = allUser.firstOrNull() { it.userId == useId }
    val userName = user?.userName ?: ""
    val userImage = user?.profileImageUrl ?: ""

    val storyViewModel: StoryViewModel = hiltViewModel()
    LaunchedEffect(true) {storyViewModel.getStories1() }


    val stories by storyViewModel.stories.collectAsState()
    val currentUserStories = stories.filter { it.userId == useId }
    val listOfImages = currentUserStories.flatMap { it.stories }
    Column(
        modifier = Modifier.padding(innerPadding)
    ) {
        Stories(listOfImages, userName, userImage)
    }
}

@Composable
fun Stories(listOfImage: List<String>, userName: String, userImage: String) {
    val pagerState = rememberPagerState(pageCount = { listOfImage.size })
    val coroutinesScope = rememberCoroutineScope()
    var currentPage = pagerState.currentPage

    Box(modifier = Modifier.fillMaxSize()) {
        StoryImage(pagerState = pagerState, imageList = listOfImage)
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent)),
                        shape = RoundedCornerShape(12.dp)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.padding(4.dp))
                listOfImage.indices.forEach { index ->
                    LinearIndicator(
                        modifier = Modifier.weight(1f),
                        startProgress = index == currentPage
                    ) {
                        coroutinesScope.launch {
                            if (currentPage < listOfImage.size - 1) {
                                currentPage++
                            }
                            pagerState.animateScrollToPage(currentPage)
                        }
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
            Row(
                modifier = Modifier.background(color = Color.Black.copy(0.6f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = userImage,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                SpacerWidth(10)
                Text(text = userName, color = Color.White, fontWeight = FontWeight.Bold)

            }
        }
    }
}


@Composable
fun StoryImage(pagerState: PagerState, imageList: List<String>) {
    HorizontalPager(
        state = pagerState,
    ) {index->
        AsyncImage(
            model = imageList[index],
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun LinearIndicator(modifier: Modifier, startProgress: Boolean = false, onAnimationEnd: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0.00f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = ""
    )

    if (startProgress) {
        LaunchedEffect(true) {
            while (progress < 1f) {
                progress += 0.01f
                delay(50)
            }
            onAnimationEnd()
        }
    }

    LinearProgressIndicator(
        modifier = modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp)),
        trackColor = Color.Black,
        color = Color.White,
        progress = animatedProgress
    )
}
