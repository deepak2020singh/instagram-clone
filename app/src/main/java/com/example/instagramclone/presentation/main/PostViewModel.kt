package com.example.instagramclone.presentation.main

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.instagramclone.domain.model.Comment
import com.example.instagramclone.domain.model.CommentReels
import com.example.instagramclone.domain.model.CommentReply
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.Reels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.String
import kotlin.collections.List

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class PostViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isPosted = MutableStateFlow<Boolean>(false)
    val isPosted: StateFlow<Boolean> = _isPosted


    @SuppressLint("SuspiciousIndentation")
    fun toggleLike(postId: String, userId: String, currentLikes: List<String>) {
        var updatedLikes = if (currentLikes.contains(userId)) {
            // If the user has already liked, remove their ID
            currentLikes - userId
        } else {
            // Otherwise, add the user's ID to the list
            currentLikes + userId
        }
        // Update FireStore with the new likes list
       db.collection("posts").document(postId).update("likes", updatedLikes)
            .addOnFailureListener { exception ->
            }
            .addOnSuccessListener {
            }
    }



    fun saveImage(
        userId: String,
        userImages: List<Uri>, // Renamed to userImages for clarity
        postDescription: String
    ) {
        val uploadedImageUrls = mutableListOf<String>() // List to store the URLs of uploaded images

        // Set isPosted to false to show "Posting..."
        _isPosted.value = false

        // Iterate over each image in the list
        for (imageUri in userImages) {
            val imageRef = storage.reference.child("post_images/${UUID.randomUUID()}.jpg")
            val uploadTask = imageRef.putFile(imageUri) // Upload each image

            // Add success and failure listeners for each image
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    uploadedImageUrls.add(uri.toString()) // Add the image URL to the list

                    if (uploadedImageUrls.size == userImages.size) {
                        // Once all images are uploaded, call saveData with all URLs
                        saveData(userId, uploadedImageUrls, postDescription)
                    }
                }.addOnFailureListener { exception ->
                    _errorMessage.value = "Image upload failed: ${exception.message}"
                    _isPosted.value = true // Reset state if upload fails
                }
            }.addOnFailureListener { exception ->
                _errorMessage.value = "Image upload failed: ${exception.message}"
                _isPosted.value = true // Reset state if upload fails
            }
        }
    }


    fun saveData(userId: String, imageUrls: List<String>, postDescription: String) {
        val postId = db.collection("posts").document().id
        val post = Post(
            imageUrls = imageUrls,
            postId = postId,
            userId = userId,
            postDescription = postDescription,
            timestamp = System.currentTimeMillis(),
            likes = emptyList()
        )
        db.collection("posts").document(postId).set(post)
            .addOnSuccessListener {
                _isPosted.value = true
            }
            .addOnFailureListener{
                _errorMessage.value = "Post failed: ${it.message}"
                _isPosted.value = true
            }
    }




    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts


    fun getPosts() {
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Optionally, order by timestamp (newest first)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _errorMessage.value = "Failed to retrieve posts: ${exception.message}"
                    return@addSnapshotListener
                }

                // Check if snapshot exists
                if (snapshot != null) {
                    val postList = snapshot.documents.map { document ->
                        val timestamp = document.getLong("timestamp") ?: 0L
                        val likes = document.get("likes") as? List<String> ?: emptyList() // Get the likes
                        Post(
                            imageUrls = document.get("imageUrls") as? List<String> ?: emptyList(),
                            postId = document.getString("postId") ?: "",
                            userId = document.getString("userId") ?: "",
                            postDescription = document.getString("postDescription") ?: "",
                            timestamp = timestamp,
                            likes = likes
                        )
                    }
                    _posts.value = postList // Update the state with the fetched posts
                }
            }
    }


    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }



    fun addComment(postId: String, comment: Comment) {
        db.collection("posts").document(postId)
            .update("comments", FieldValue.arrayUnion(comment))
            .addOnSuccessListener {
                Log.d("PostViewModel", "Comment added successfully")
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = "Failed to add comment: ${exception.message}"
            }
    }

    fun getComments(postId: String, onCommentsFetched: (List<Comment>) -> Unit) {
        db.collection("posts")
            .document(postId)
            .addSnapshotListener { documentSnapshot, _ ->
                val post = documentSnapshot?.toObject(Post::class.java)
                onCommentsFetched(post?.comments ?: emptyList())
            }
    }


    fun addReels(userId: String, reels: Uri, storyDuration: Int = 15){
        val reelRef = storage.reference.child("reel_video/${UUID.randomUUID()}.jpg")
        val uploadTask = reelRef.putFile(reels) // Upload each image
        uploadTask.addOnSuccessListener{
            reelRef.downloadUrl.addOnSuccessListener { uri ->
                uploadReels(userId, uri.toString(), storyDuration)
            }.addOnFailureListener { exception ->
                _errorMessage.value = "Reel image upload failed: ${exception.message}"
            }
        }.addOnFailureListener{

        }
    }

    private fun uploadReels(userId:String, reels:  String, duration:Int) {
        val reelId = db.collection("reels").document().id
        val reel = Reels(
         reelId = reelId,
         userId = userId,
         reels = reels,
         duration = duration,
         timestamp = System.currentTimeMillis(),
            likes = emptyList()
        )
        db.collection("reels").document(reelId).set(reel)
            .addOnSuccessListener {
                _isPosted.value = true
            }
            .addOnFailureListener{
                _errorMessage.value = "Post failed: ${it.message}"
                _isPosted.value = true
            }
    }


    @SuppressLint("SuspiciousIndentation")
    fun toggleLikeReel(reelId: String, userId: String, currentLikes: List<String>) {
        var updatedLikes = if (currentLikes.contains(userId)) {
            // If the user has already liked, remove their ID
            currentLikes - userId
        } else {
            // Otherwise, add the user's ID to the list
            currentLikes + userId
        }
        // Update FireStore with the new likes list
        db.collection("reels").document(reelId).update("likes", updatedLikes)
            .addOnFailureListener { exception ->
            }
            .addOnSuccessListener {
            }
    }




    private val _reels = MutableStateFlow<List<Reels>>(emptyList())
    val reels: StateFlow<List<Reels>> = _reels

    fun getAllReels(){
        db.collection("reels").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener{
            val reels = it.toObjects(Reels::class.java)
            _reels.value = reels
        }.addOnFailureListener{
            _errorMessage.value = "Failed to fetch Reels: ${it.message}"
        }
    }


    fun addComment(reelId: String, comment: String){
        val commentId = db.collection("reels").document(reelId).collection("comments").document().id
        val comment = CommentReels(
            commentId = commentId,
            userId = auth.currentUser?.uid ?: "",
            text = comment,
            timestamp = System.currentTimeMillis(),
            replies = emptyList()
        )
        db.collection("reels")
            .document(reelId)
            .collection("comments")
            .document(commentId)
            .set(comment).addOnSuccessListener{

            }.addOnFailureListener{

            }
    }



    private val _comments = MutableStateFlow<List<CommentReels>>(emptyList())
    val comments: StateFlow<List<CommentReels>> = _comments

            fun getReelsComments(reelId: String,) {
                db.collection("reels")
                    .document(reelId)
                    .collection("comments")
                    .orderBy("timestamp")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val comments = snapshot.toObjects(CommentReels::class.java)
                        val commentsWithReplies = mutableListOf<CommentReels>()

                        for (comment in comments) {
                            // Fetch replies for each comment
                            db.collection("reels")
                                .document(reelId)
                                .collection("comments")
                                .document(comment.commentId)
                                .collection("replies")
                                .orderBy("timestamp")
                                .get()
                                .addOnSuccessListener { replySnapshot ->
                                    val replies = replySnapshot.toObjects(CommentReply::class.java)
                                    // Merge replies into the comment object
                                    val commentWithReplies = comment.copy(replies = replies)
                                    commentsWithReplies.add(commentWithReplies)

                                    // Only update UI when all replies for all comments are fetched
                                    if (commentsWithReplies.size == comments.size) {
                                        _comments.value = commentsWithReplies
                                    }
                                }
                                .addOnFailureListener { e ->
                                    _errorMessage.value = "Failed to fetch replies: ${e.message}"
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        _errorMessage.value = "Failed to fetch comments: ${e.message}"
                    }
            }

            // Function to add a reply
            fun addReplyToComment(reelId: String, commentId: String, replyText: String) {
                val replyId = db.collection("reels")
                    .document(reelId)
                    .collection("comments")
                    .document(commentId)
                    .collection("replies")
                    .document().id  // Auto-generated ID for the reply

                val reply = CommentReply(
                    commentId = replyId,
                    userId = auth.currentUser?.uid ?: "",
                    userName = auth.currentUser?.displayName ?: "Anonymous",
                    profilePicUrl = auth.currentUser?.photoUrl.toString(),
                    relyText = replyText,
                    timestamp = System.currentTimeMillis()
                )

                db.collection("reels")
                    .document(reelId)
                    .collection("comments")
                    .document(commentId)
                    .collection("replies")
                    .document(replyId)
                    .set(reply)
                    .addOnSuccessListener {
                        println("Reply added successfully!")
                    }
                    .addOnFailureListener { e ->
                        _errorMessage.value = "Failed to add reply: ${e.message}"
                    }
            }
        }



