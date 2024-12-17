package com.example.instagramclone.presentation.auth

import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.instagramclone.presentation.main.StoryViewModel
import com.example.instagramclone.utlis.SharPref
import com.example.instagramclone.utlis.SpacerWidth
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddStory(innerPaddingValues: PaddingValues) {
    val storyViewModel: StoryViewModel = hiltViewModel()
    val error by storyViewModel.errorMessage.collectAsState()
    val isPosted by storyViewModel.isPosted.collectAsState()
    val context = LocalContext.current
    var storiesUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> storiesUris = uris }
    )
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    LaunchedEffect(isPosted) {
        if (isPosted) {
            storiesUris = emptyList()
        }
    }
    if (error.isNotEmpty()) {
        Text(text = error)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(innerPaddingValues).padding(horizontal = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "New Story")
            Text(text = "Next")
        }
        Button(
            onClick = { launcher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) }
        ) {
            Text(text = "Select image")
        }

        if (storiesUris.isEmpty()) {
            Text("No images selected", modifier = Modifier.padding(16.dp))
        } else {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(storiesUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Add Image",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .background(color = Color.Gray)
                            .clickable {},
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Button(
            onClick = { storyViewModel.saveStory(userId, storiesUris)},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Upload story")
        }

    }

}