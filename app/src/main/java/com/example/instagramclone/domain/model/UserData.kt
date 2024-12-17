package com.example.instagramclone.domain.model


data class UserModel(
    val userId: String = "",
    val userName: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val link: String = "",
    val followers: List<String> = emptyList<String>(),
    val following: List<String> = emptyList()
)

data class Post(
    val imageUrls: List<String> = emptyList(),
    val postId: String = "",
    val userId: String = "",
    val postDescription: String = "",
    val timestamp: Long = 0L,
    val likes: List<String> = emptyList(),
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val comment: String = "",
    val commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val profilePicUrl: String = "",
    val commentText: String = "",
    val timestamp: Long = 0L,
    val replies: List<CommentReply> = emptyList()
)

data class Story(
    val storyId: String = "",
    val userId: String = "",
    val userName: String = "",
    val profilePicUrl: String = "",
    val stories: List<String> = emptyList(),
    val isViewed: Boolean = false,
    val storyDuration: Int = 15,
    val timestamp: Long = 0L,
)

data class Reels(
    val reelId: String = "",
    val userId: String = "",
    val reels: String = "",
    val duration: Int = 0,
    val timestamp: Long = 0L,
    val likes: List<String> = emptyList(),
)

data class CommentReels(
    val commentId: String = "",
    val userId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val replies: List<CommentReply> = emptyList()
)


data class CommentReply(
    val commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val profilePicUrl: String = "",
    val relyText: String = "",
    val timestamp: Long = 0L
)