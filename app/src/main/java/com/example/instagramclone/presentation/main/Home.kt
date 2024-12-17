@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("DEPRECATION")

package com.example.instagramclone.presentation.main


import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.instagramclone.domain.model.Comment
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.presentation.auth.AuthViewModel
import com.example.instagramclone.presentation.auth.Routes
import com.example.instagramclone.utlis.SpacerHeight
import com.example.instagramclone.utlis.SpacerWidth
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun Home(innerPadding: PaddingValues, navController1: NavHostController, navController: NavHostController) {
    val postViewModel: PostViewModel = hiltViewModel()
    val posts: List<Post> by postViewModel.posts.collectAsState()
    val error by postViewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    LaunchedEffect(true) { postViewModel.getPosts() }

    if (error.isNotEmpty()) {
        Text(text = error)
    }
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                title = { Text("Instagram", color = Color.White) },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) {values ->
            LazyColumn(state = scrollState, modifier = Modifier.padding(values)) {
                // Story Section
                item {
                    StorySection(navController, context)
                    HorizontalDivider()
                    SpacerHeight(4)
                }
                // Posts
                items(posts) { post ->
                    InstagramPostScreen(
                        post,
                        navController1,
                        currentUserId
                    )
                }
               item{
                   SpacerHeight(40)
               }
            }
        }
    }


@Composable
fun StorySection(navController: NavHostController, context: Context) {
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    val storyViewModel: StoryViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val allUser by authViewModel.allUser.collectAsState()

    LaunchedEffect(true) { authViewModel.getAllUsers() }
    LaunchedEffect(true) { storyViewModel.getStories1() }
    val user = allUser.firstOrNull { it.userId == userId }
    val userName = user?.userName ?: ""
    val userImage = user?.profileImageUrl ?: ""

    val stories by storyViewModel.stories.collectAsState()
    val errorMessage by storyViewModel.errorMessage.collectAsState()

    // Filter stories
    val currentUserStories = stories.filter { it.userId == userId }
    val otherUserStories = stories.filter { it.userId != userId }


    // Group stories by userId to combine the stories of the same user
    val groupedStories = otherUserStories.groupBy { it.userId }

    if (errorMessage.isNotEmpty()) {
        Text(text = errorMessage, color = Color.Red)
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Display current user's story section
        item {
            Column(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                if (currentUserStories.isEmpty()) {
                    Box(modifier = Modifier
                        .size(60.dp)
                        .clickable { navController.navigate("addStory") }) {
                        Image(
                            painter = rememberAsyncImagePainter(""),
                            contentDescription = "",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentScale = ContentScale.Crop
                        )
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                    Text(
                        "Add Story",
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Box(modifier = Modifier.size(60.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(userImage),
                            contentDescription = "",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .clickable {
                                    navController.navigate("storyDetail/${userId}")
                                },
                            contentScale = ContentScale.Crop
                        )
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clickable { navController.navigate("addStory") }
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }

        // Loop through grouped user stories
        items(groupedStories.keys.toList()) { userId ->
            val user = allUser.firstOrNull { it.userId == userId }
            val userName = user?.userName ?: "Unknown User"
            val userImage = user?.profileImageUrl ?: ""
            val userStories = groupedStories[userId] ?: emptyList()

            Column(modifier = Modifier.padding(top = 2.dp)) {
                // User profile image and name
                AsyncImage(
                    model = userImage,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .border(
                            2.dp,
                            brush = Brush.linearGradient(listOf(Color.Red, Color.Yellow)),
                            CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { navController.navigate("otherStory/${userId}") }
                )
                Text(text = userName)
            }
        }
    }
}





@Composable
fun InstagramPostScreen(post: Post, navController: NavHostController, currentUserId: String) {
    val context = LocalContext.current
    val postViewModel: PostViewModel = hiltViewModel()
    val timeFormat = remember { postViewModel.formatTimestamp(post.timestamp) }
    val viewModel: AuthViewModel = hiltViewModel()

    var isLiked = remember { mutableStateOf(post.likes.contains(currentUserId)) }
    var likeCount by remember { mutableIntStateOf(post.likes.size) }

    LaunchedEffect(true) { viewModel.getAllUsers() }

    val allUser by viewModel.allUser.collectAsState()
    val user = allUser.firstOrNull { it.userId == post.userId}

    val currentUser = allUser.firstOrNull { it.userId == currentUserId }
    val currentUserName = currentUser?.userName ?: ""
    val currentUserImage = currentUser?.profileImageUrl ?: ""

    val userName1 = user?.userName ?: "Unknown"
    val imageUrl1 = user?.profileImageUrl ?: ""
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { post.imageUrls.size })

    var bottomSheetState by remember { mutableStateOf(false) }
    val commentText = remember { mutableStateOf("") }

    // Observing comments dynamically
    val comments = remember { mutableStateListOf<Comment>() }
    LaunchedEffect(post.postId) {
        postViewModel.getComments(post.postId) { fetchedComments ->
            comments.clear()
            comments.addAll(fetchedComments)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header with user info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile image
                AsyncImage(
                    model = imageUrl1,
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = Color.Gray)
                        .clickable {
                            navController.navigate("${Routes.Profile.route}/${post.userId}")
                        },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                // User name
                Column {
                    Text(text = userName1)
                    Text(text = timeFormat)
                }
            }
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
        }
        SpacerHeight(5)
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { pageIndex ->
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)) {
                AsyncImage(
                    model = post.imageUrls[pageIndex],
                    contentDescription = "Post image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Pager indicator text
                if (pagerState.pageCount > 1) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                        modifier = Modifier
                            .padding(top = 2.dp, end = 2.dp)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .align(Alignment.TopEnd)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        if (pagerState.pageCount > 1) {
            PagerIndicator(pagerState)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like post",
                        tint = if (isLiked.value) Color.Red else Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                isLiked.value = !isLiked.value
                                // Update the likes list in FireStore
                                postViewModel.toggleLike(post.postId, currentUserId, post.likes)
                                // Update the like count based on the new like status
                                likeCount = if (isLiked.value) likeCount + 1 else likeCount - 1
                            }
                    )
                    Text(text = "$likeCount likes") // Show the like count
                }
                SpacerWidth(10)
                Row {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Comment",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                bottomSheetState = true
                            }
                    )
                    SpacerWidth(3)
                    Text(text = "${comments.size}")
                }
                SpacerWidth(10)
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { shareVia(context, userName1, imageUrl1) }
                )

            }
            Icon(imageVector = Icons.Outlined.Settings, contentDescription = "save", modifier = Modifier
                .size(24.dp)
                .clickable {})
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "${userName1} ${post.postDescription}", modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(modifier = Modifier.height(10.dp))
    }
    if (bottomSheetState) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { true }
            ),
            onDismissRequest = { bottomSheetState = false },
            shape = BottomSheetDefaults.ExpandedShape
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Comments")
                // LazyColumn to show the list of comments
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(comments) { comment ->
                        CommentItem(comment = comment, currentUserId, postViewModel, post.postId)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = commentText.value,
                        onValueChange = { commentText.value = it },
                        placeholder = { Text(text = "Enter comment") }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send comment", modifier = Modifier.clickable{
                        val newComment = Comment(
                            commentId = UUID.randomUUID().toString(),
                            userId = currentUserId,
                            userName = currentUserName,
                            profilePicUrl = currentUserImage,
                            commentText = commentText.value,
                            timestamp = System.currentTimeMillis()
                        )
                        postViewModel.addComment(post.postId, newComment)
                        bottomSheetState = false
                        commentText.value = ""
                    })
                }

            }

        }
    }
}


fun shareVia(context: Context, userName: String, profileImageUrl: String) {
    val shareText = "Check out the profile of $userName. https://www.instagram.com/$userName"
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)  // Share text with user profile details
    }
    // Optional: If you'd like to share the profile image as well
    // putExtra(Intent.EXTRA_STREAM, Uri.parse(profileImageUrl))  // For sharing images
    try {
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    } catch (e: Exception) {
        Toast.makeText(context, "No apps available for sharing", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun PagerIndicator(pagerState: PagerState) {
    var coroutine = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Loop through all the pages to create a dot for each page
        for (index in 0 until pagerState.pageCount) {
            // Indicator dot - A different color for the selected dot
            val isSelected = pagerState.currentPage == index
            val dotColor = if (isSelected) Color.Blue else Color.Gray
            val dotSize = if (isSelected) 12.dp else 8.dp
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(dotSize)
                    .background(dotColor, CircleShape)
                    .clickable {
                        if (pagerState.currentPage != index) {
                            coroutine.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    }
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    currentUserId: String,
    postViewModel: PostViewModel,
    postId: String
) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Spacer(modifier = Modifier.width(10.dp))
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncImage(
                model = comment.profilePicUrl,
                contentDescription = "User Image",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "${comment.userName} ${comment.commentText}",
                fontWeight = FontWeight.Bold,
            )
        }
    }
}





