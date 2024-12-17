@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.instagramclone.presentation.main


import android.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.instagramclone.domain.model.CommentReels
import com.example.instagramclone.domain.model.CommentReply
import com.example.instagramclone.domain.model.Reels
import com.example.instagramclone.domain.model.UserModel
import com.example.instagramclone.presentation.auth.AuthViewModel
import com.example.instagramclone.utlis.SpacerWidth
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Video(innerPaddingValues: PaddingValues) {
    val context = LocalContext.current
    val reelsViewModel: PostViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val reels = reelsViewModel.reels.collectAsState().value
    val user = authViewModel.allUser.collectAsState().value

    LaunchedEffect(Unit) {
        reelsViewModel.getAllReels()
        authViewModel.getAllUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
            .padding(horizontal = 4.dp)
    ) {
        ReelsPage(context, reels, user, reelsViewModel)
    }
}

@Composable
fun ReelsPage(context: Context, reels: List<Reels>, user: List<UserModel>, reelsViewModel: PostViewModel) {
    val pagerState = rememberPagerState(pageCount = { reels.size }, initialPage = 0)

    VerticalPager(
        state = pagerState,
        userScrollEnabled = true,
        modifier = Modifier.fillMaxWidth()
    ) { index ->
        // Pass the current reel to ReelItem composable
        ReelItem(reel = reels[index], user, reelsViewModel)
    }
}

@Composable
fun ReelItem(reel: Reels, user: List<UserModel>, reelsViewModel: PostViewModel) {
    val findUser = user.firstOrNull{it.userId == reel.userId}
    var userName = findUser?.userName
    var profileImage = findUser?.profileImageUrl
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    var isLiked = remember { mutableStateOf(reel.likes.contains(currentUserId)) }
    var likeCount by remember { mutableIntStateOf(reel.likes.size) }

    LaunchedEffect(true) {
        reelsViewModel.getReelsComments(reel.reelId)
    }
   //  val comments = reelsViewModel.comments.collectAsState().value

   // Log.d("Comments", comments.toString())


    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse(reel.reels) // Use the dynamic reel URL
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
        }
    }

    // Dispose ExoPlayer when composable leaves
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(exoPlayer) {
        exoPlayer.play()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                // Toggle play/pause on click
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause() // Pause if playing
                } else {
                    exoPlayer.play() // Play if paused
                }
            }
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false // Disable default controls
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay for username and profile image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent background
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                // Profile Picture - Dynamic data
                Image(
                    painter = rememberAsyncImagePainter(profileImage),
                    contentDescription = "User Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .border(2.dp, Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "$userName", // Replace with dynamic user name
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        val comments = reelsViewModel.comments.collectAsState().value

        Log.d("Comments4", comments.toString())
        var showBottomSheet = remember { mutableStateOf(false) }
        var reelsComments = remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                .padding(vertical = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isLiked.value = !isLiked.value
                    reelsViewModel.toggleLikeReel(reel.reelId, currentUserId, reel.likes)
                    likeCount = if (isLiked.value) likeCount + 1 else likeCount - 1
                }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (isLiked.value) Color.Red else Color.White,
                    )
                }
                Text(text = "$likeCount Likes", color = Color.White, fontSize = 12.sp)
            }
Row(verticalAlignment = Alignment.CenterVertically) {
    IconButton(onClick = { showBottomSheet.value = !showBottomSheet.value }) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Comment",
            tint = Color.White
        ) }
    Text(text = "${comments.size}", color = Color.White)
}
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "https://reels/")
                context.startActivity(intent)
            }){
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }

            if (showBottomSheet.value){
                ModalBottomSheet(
                    sheetState = rememberModalBottomSheetState(
                        skipPartiallyExpanded = true,
                        confirmValueChange = { true }
                    ),
                    onDismissRequest = { showBottomSheet.value = false },
                    shape = BottomSheetDefaults.ExpandedShape
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Comment")
                        LazyColumn(
                            content = {
                                items(comments){it->
                                   CommonComments(it, reel, reelsViewModel)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.padding(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = reelsComments.value,
                                onValueChange = { reelsComments.value = it },
                                placeholder = { Text("write comment") }
                            )
                            SpacerWidth(6)
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "",
                                modifier = Modifier.clickable{
                                    reelsViewModel.addComment(reel.reelId, reelsComments.value)
                                    showBottomSheet.value = false
                                    reelsComments.value = ""
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommonComments(comments: CommentReels, reel: Reels, reelsViewModel1: PostViewModel) {
    var addReply = remember { mutableStateOf("") }
    var repToggle = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = "", contentDescription = "",
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Gray), contentScale = ContentScale.Crop)
            SpacerWidth(4)
            Text(text = comments.text,
                modifier = Modifier.clickable { repToggle.value = !repToggle.value })
        }
        if (comments.replies.isNotEmpty()) {
            comments.replies.forEach {
                ReplyItem(reply = it)
            }
        }


    if (repToggle.value) {
        Row {
            OutlinedTextField(
                value = addReply.value,
                onValueChange = { addReply.value = it },
                placeholder = { Text("Reply") }
            )
            SpacerWidth(6)
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "",
                modifier = Modifier.clickable {
                    reelsViewModel1.addReplyToComment(reel.reelId, comments.commentId, addReply.value)
                    addReply.value = ""
                    repToggle.value = false
                }
            )
        }
    }
    }
}

@Composable
fun ReplyItem(reply: CommentReply) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(text = reply.relyText)
    }
}

