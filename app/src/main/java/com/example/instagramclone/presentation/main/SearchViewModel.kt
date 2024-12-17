package com.example.instagramclone.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.instagramclone.domain.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    // StateFlow to hold the list of all users
    private val _allUsers = MutableStateFlow<List<UserModel>>(emptyList())
    val allUsers: StateFlow<List<UserModel>> = _allUsers

    // StateFlow to hold the filtered list of users based on the search query
    private val _filteredUsers = MutableStateFlow<List<UserModel>>(emptyList())
    val filteredUsers: StateFlow<List<UserModel>> = _filteredUsers

    fun getAllUsers(currentUserId: String) {
        db.collection("users")
            .whereNotEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                val usersList = result.map { document ->
                    UserModel(
                        userId = document.getString("userId") ?: "",
                        userName = document.getString("userName") ?: "",
                        name = document.getString("name") ?: "",
                        email = document.getString("email") ?: "",
                        profileImageUrl = document.getString("profileImageUrl") ?: "",
                        bio = document.getString("bio") ?: "",
                        followers = document.get("followers") as? List<String> ?: emptyList(),
                        following = document.get("following") as? List<String> ?: emptyList()
                    )
                }
                _allUsers.value = usersList
                _filteredUsers.value = usersList // Initially, show all users
            }
            .addOnFailureListener { exception ->

            }
    }

    // Function to filter users by username
    fun filterUsers(query: String) {
        if (query.isEmpty()) {
            _filteredUsers.value = _allUsers.value // Show all users if query is empty
        } else {
            _filteredUsers.value = _allUsers.value.filter {
                it.userName.contains(query, ignoreCase = true)
            }
        }
    }

    // Function to follow a user
    fun followUser(userId: String, currentUserId: String) {
        val userRef = db.collection("users").document(userId)
        val currentUserRef = db.collection("users").document(currentUserId)
        db.runBatch { batch ->
            // Add current user to the following list of the target user
            batch.update(userRef, "followers", FieldValue.arrayUnion(currentUserId))

            // Add the target user to the following list of the current user
            batch.update(currentUserRef, "following", FieldValue.arrayUnion(userId))
        }.addOnSuccessListener {
            // Update the local state to reflect the changes
            updateFollowStatus(userId, currentUserId, true)
        }.addOnFailureListener { exception ->
            Log.e("FollowError", "Failed to follow user: ${exception.message}")
        }
    }

    // Function to unfollow a user
    fun unfollowUser(userId: String, currentUserId: String) {
        val userRef = db.collection("users").document(userId)
        val currentUserRef = db.collection("users").document(currentUserId)
        db.runBatch { batch ->
            // Remove current user from the following list of the target user
            batch.update(userRef, "followers", FieldValue.arrayRemove(currentUserId))
            // Remove the target user from the following list of the current user
            batch.update(currentUserRef, "following", FieldValue.arrayRemove(userId))
        }.addOnSuccessListener {
            // Update the local state to reflect the changes
            updateFollowStatus(userId, currentUserId, false)
        }.addOnFailureListener { exception ->
            Log.e("UnfollowError", "Failed to unfollow user: ${exception.message}")
        }
    }

    // Update local state to reflect the follow/unfollow action
    private fun updateFollowStatus(userId: String, currentUserId: String, isFollowing: Boolean) {
        _allUsers.value = _allUsers.value.map {
            if (it.userId == userId) {
                val updatedFollowers = if (isFollowing) {
                    it.followers + currentUserId
                } else {
                    it.followers - currentUserId
                }
                val updatedFollowing = if (isFollowing) {
                    it.following + userId
                } else {
                    it.following - userId
                }

                it.copy(followers = updatedFollowers, following = updatedFollowing)
            } else {
                it
            }
        }

        _filteredUsers.value = _filteredUsers.value.map {
            if (it.userId == userId) {
                it.copy(
                    followers = if (isFollowing) it.followers + currentUserId else it.followers - currentUserId,
                    following = if (isFollowing) it.following + userId else it.following - userId
                )
            } else {
                it
            }
        }
    }
}


