package com.example.instagramclone.presentation.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.instagramclone.domain.model.Story
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class StoryViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ViewModel() {

    val currentUsers = auth.currentUser!!.uid
    // StateFlow to hold the list of stories
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    // StateFlow to handle error messages
    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isPosted = MutableStateFlow<Boolean>(false)
    val isPosted: StateFlow<Boolean> = _isPosted

    fun saveStory(userId: String, stories: List<Uri>, storyDuration: Int = 15) {
        val uploadedStoryUrls = mutableListOf<String>()

        val storyId = UUID.randomUUID().toString()
        // Upload each story image
        for (storyUri in stories) {
            val storyRef = storage.reference.child("story_images/${UUID.randomUUID()}.jpg")
            val uploadTask = storyRef.putFile(storyUri)

            uploadTask.addOnSuccessListener {
                storyRef.downloadUrl.addOnSuccessListener { uri ->
                    uploadedStoryUrls.add(uri.toString())

                    if (uploadedStoryUrls.size == stories.size) {
                        saveStoryData(userId, uploadedStoryUrls, storyDuration)
                    }
                }.addOnFailureListener { exception ->
                    _errorMessage.value = "Story image upload failed: ${exception.message}"
                }
            }.addOnFailureListener { exception ->
                _errorMessage.value = "Story image upload failed: ${exception.message}"
            }
        }
    }

    private fun saveStoryData(
        userId: String,
        stories: List<String>,
        storyDuration: Int
    ) {
        val storyId = db.collection("stories").document().id
        val story = Story(
            storyId = storyId,
            userId = userId,
            stories = stories,
            isViewed = false,
            storyDuration = storyDuration,
            timestamp = System.currentTimeMillis()
        )
        // Save the story to FireStore
        db.collection("stories").document(storyId).set(story)
            .addOnSuccessListener {
                _isPosted.value = true
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = "Failed to save story data: ${exception.message}"
            }
    }


    fun getStories1() {
        val currentTime = System.currentTimeMillis()
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
        db.collection("stories")
            .whereGreaterThanOrEqualTo("timestamp", currentTime - twentyFourHoursInMillis)
            .get()
            .addOnSuccessListener { result ->
                _stories.value = result.toObjects(Story::class.java)
            }
            .addOnFailureListener{
                _errorMessage.value = "Failed to get stories"
            }
    }





}
