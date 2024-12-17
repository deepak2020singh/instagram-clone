package com.example.instagramclone.presentation.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import com.example.instagramclone.utlis.SpacerHeight
import com.example.instagramclone.utlis.SpacerWidth
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalCoilApi::class)
@Composable
fun AddPost(innerPaddingValues: PaddingValues) {
    val postViewModel: PostViewModel = hiltViewModel()
    val isPosted by postViewModel.isPosted.collectAsState()


    var postDescription by remember { mutableStateOf("") }
    var postImage by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var addReel by remember { mutableStateOf<Uri?>(null)}

    var context = LocalContext.current

    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { postImage = it }
        LaunchedEffect(isPosted) {
            if (isPosted){
                postDescription = ""
               postImage = emptyList()
            }
        }

    val launcher2 = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        addReel = it
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPaddingValues)
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("New Post", style = MaterialTheme.typography.bodyMedium)
            Text("Next", modifier = Modifier.clickable(onClick = {}))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = { launcher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) }
            ) {
                Text(text = "Images")
            }
            SpacerWidth(5)
            Button(
                onClick = { launcher2.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly)) }
            ) {
                Text(text = "Reels")
            }
        }

        SpacerHeight(5)
        LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(postImage) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Add Image",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .background(color = Color.Gray)
                            .clickable {
                                // Optional click actions
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        Spacer(modifier = Modifier.height(16.dp))

        // Caption Input
        if (postImage.isNotEmpty()) {
            TextField(
                value = postDescription,
                onValueChange = { postDescription = it },
                label = { Text("Caption") },
                modifier = Modifier.fillMaxWidth()
            )
        SpacerHeight(16)
            Button(
                onClick = { postViewModel.saveImage(userId, postImage, postDescription) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "add Post")
            }
        }

        if (addReel != null) {
            Button(
                onClick = { postViewModel.addReels(userId, addReel!!) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "add reel")
            }
        }else{

        }
    }
}