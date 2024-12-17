package com.example.instagramclone.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.presentation.auth.AuthViewModel
import com.example.instagramclone.presentation.auth.Routes
import com.example.instagramclone.utlis.SpacerHeight
import com.example.instagramclone.utlis.SpacerWidth
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Profile(
    innerPadding: PaddingValues,
    navController1: NavHostController,
    navController: NavHostController,
    userId: String? = null
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val firebaseUser by viewModel.firebaseUser.observeAsState(null)


    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null) {
            navController.navigate("login"){
                popUpTo("mainScreen"){
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.getAllUsers()
    }

    val allUser by viewModel.allUser.collectAsState()
    val user = allUser.firstOrNull { it.userId == userId }

    val postViewModel: PostViewModel = hiltViewModel()
    LaunchedEffect(true) {
        postViewModel.getPosts()
    }

    val posts: List<Post> by postViewModel.posts.collectAsState()
    val getUserPosts: List<Post> = posts.filter { it.userId == userId }
    var listOfImg = listOf<String>()
    val imageList = getUserPosts.map { value ->
        listOfImg = listOfImg + value.imageUrls
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Profile Screen")
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "",
                modifier = Modifier.clickable {navController1.navigate("profileMore")}
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Image(
                    painter = rememberAsyncImagePainter(user?.profileImageUrl ?: ""),
                    contentDescription = "",
                    modifier = Modifier.size(45.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                SpacerWidth(5)
                Text(text = user?.userName ?: "")
            }
            SpacerWidth(10)
            Row {
                Column {
                    Text(text = listOfImg.size.toString(), modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
                    Text(text = "Posts")
                }
                SpacerWidth(10)
                Column(
                    modifier = Modifier.clickable{navController1.navigate("follow/${userId}")}
                ) {
                    Text(text = "${user?.followers?.size}", modifier = Modifier.width(63.dp), textAlign = TextAlign.Center)
                    Text(text = "Followers")
                }
                SpacerWidth(10)
                Column(
                    modifier = Modifier.clickable{navController1.navigate("following/${userId}")}
                ) {
                    Text(text = "${user?.following?.size}", modifier = Modifier.width(63.dp), textAlign = TextAlign.Center)
                    Text(text = "Following")
                }
            }
        }
        Text(text = "Email: ${user?.email}")
        Text(text = "Bio: ${user?.bio}")
        Text(text = "www.kites.com")
        SpacerHeight(6)
        if (FirebaseAuth.getInstance().currentUser?.uid == userId) {
            Text(
                text = "Edit Profile",
                modifier = Modifier.clickable {
                    navController1.navigate(Routes.EditProfile.route)
                }
            )
        }
        SpacerHeight(6)

        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(listOfImg) { it ->
                AsyncImage(
                    model = it,
                    contentDescription = "",
                    modifier = Modifier.width(100.dp).height(120.dp).padding(4.dp)
                        .background(color = Color.Gray),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

