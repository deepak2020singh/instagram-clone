package com.example.instagramclone.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.instagramclone.R
import com.example.instagramclone.presentation.main.AddPost
import com.example.instagramclone.presentation.main.EditProfile
import com.example.instagramclone.presentation.main.FollowScreen
import com.example.instagramclone.presentation.main.FollowingScreen
import com.example.instagramclone.presentation.main.Home
import com.example.instagramclone.presentation.main.Profile
import com.example.instagramclone.presentation.main.Search
import com.example.instagramclone.presentation.main.Video
import com.google.firebase.auth.FirebaseAuth

sealed class Routes(val icons: Int, val labels: String, val route: String) {
    data object Home : Routes(R.drawable.home, "Home", "home")
    data object Profile : Routes(R.drawable.profile_user, "Profile", "profile")
    data object Search : Routes(R.drawable.search, "Search", "search")
    data object Video : Routes(R.drawable.reel, "Video", "video")
    data object AddPost : Routes(R.drawable.add, "Add", "add")
    data object EditProfile : Routes(R.drawable.reel, "Edit Profile", "edit_profile")
    data object ProfileMore : Routes(R.drawable.profile_user, "Profile More", "profile_more")
}

@Composable
fun MainScreen(innerPadding: PaddingValues, navController: NavHostController) {
    val navController1 = rememberNavController()
    val navBackStackEntry by navController1.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    val viewModel: AuthViewModel = hiltViewModel()
    LaunchedEffect(true) {
        viewModel.getAllUsers()
    }
    val allUser by viewModel.allUser.collectAsState()

    val currentUser = allUser.firstOrNull { it.userId == currentUserId }

    val userImage = currentUser?.profileImageUrl ?: ""

    Column(modifier = Modifier.fillMaxWidth()) {
        val listItems = listOf(
            Routes.Home,
            Routes.Search,
            Routes.AddPost,
            Routes.Video,
            Routes.Profile,
        )

        Scaffold(
            bottomBar = {
                if (currentDestination != Routes.AddPost.route) {
                    BottomAppBar(modifier = Modifier.height(49.dp))
                    {
                        NavigationBar {
                            listItems.forEach { route ->
                                NavigationBarItem(
                                    selected = currentDestination == route.route,
                                    onClick = {
                                        when (route) {
                                        Routes.Profile -> {
                                            // Navigate to profile screen with current user's userId
                                            navController1.navigate("profile/$currentUserId") {
                                                popUpTo(navController1.graph.startDestinationId)
                                                launchSingleTop = true
                                            }
                                        }
                                        else -> {
                                            // For other routes, navigate normally
                                            navController1.navigate(route.route) {
                                                popUpTo(navController1.graph.startDestinationId)
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                    },
                                    icon = {
                                        when(route){
                                            Routes.Profile -> {
                                                Image(
                                                    painter = rememberAsyncImagePainter(userImage),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp).clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )

                                            }
                                            else -> {
                                                Icon(
                                                    painter = rememberAsyncImagePainter(route.icons),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp),
                                                    )

                                            } }
                                           },
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController = navController1, startDestination = Routes.Home.route) {
                composable(Routes.Home.route) {
                    Home(innerPadding, navController1, navController)
                }
                composable("profileMore"){
                    ProfileMore(innerPadding, navController, navController1)
                }
                // Profile screen now receives the targetUserId as a parameter
                composable(Routes.EditProfile.route) {
                    EditProfile(innerPadding)
                }
                composable("${Routes.Profile.route}/{userId}",
                    arguments = listOf(
                    navArgument("userId"){ type = NavType.StringType }
                )) {backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")
                        Profile(
                            innerPadding = innerPadding,
                            navController1 = navController1,
                            navController = navController,
                            userId = userId
                        )
                }


                composable("following/{userId}",
                    arguments = listOf(
                        navArgument("userId"){ type = NavType.StringType }
                    )){backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")
                    FollowingScreen(innerPadding, userId, navController1)
                }

                composable("follow/{userId}",
                    arguments = listOf(
                        navArgument("userId"){ type = NavType.StringType }
                    )){backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")
                    FollowScreen(innerPadding, userId, navController1)
                }
                composable(Routes.Search.route) {
                    Search(innerPadding, navController1)
                }
                composable(Routes.Video.route) {
                    Video(innerPadding)
                }
                composable(Routes.AddPost.route) {
                    AddPost(innerPadding) // Render Add Post screen without the bottom bar
                }
            }
        }
    }
}
