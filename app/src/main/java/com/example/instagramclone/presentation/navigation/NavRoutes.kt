package com.example.instagramclone.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.instagramclone.presentation.auth.AddStory
import com.example.instagramclone.presentation.auth.AuthViewModel
import com.example.instagramclone.presentation.auth.ForgetPassword
import com.example.instagramclone.presentation.auth.Login
import com.example.instagramclone.presentation.auth.MainScreen
import com.example.instagramclone.presentation.auth.OtherStoryDetail
import com.example.instagramclone.presentation.auth.SignUp
import com.example.instagramclone.presentation.auth.Splash
import com.example.instagramclone.presentation.auth.StoryDetail

@Composable
fun NavRoutes(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    var viewModel: AuthViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            Splash(navController = navController)
        }
        composable("login") {
            Login(navController = navController, viewModel)
        }
        composable("signUp") {
            SignUp(navController, viewModel)
        }
        composable("forgetPassword") {
            ForgetPassword()
        }
        composable("mainScreen") {
            MainScreen(innerPadding = innerPadding, navController = navController)
        }
        composable("addStory") {
            AddStory(innerPadding)
        }
        composable("otherStory/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )) {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            OtherStoryDetail(innerPadding, userId!!)
        }
        composable(
            "storyDetail/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            StoryDetail(innerPadding, userId!!)
        }


    }
}