package com.example.instagramclone.presentation.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagramclone.domain.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject
import kotlin.String



@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
): ViewModel() {
    private val _firebaseUser = MutableLiveData<FirebaseUser?>(null)
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    private val _errorMessage = MutableLiveData<String>("")
    val errorMessage: LiveData<String> = _errorMessage

    init {
    _firebaseUser.value = auth.currentUser
}

    private val _allUser = MutableStateFlow<List<UserModel>>(emptyList())
    val allUser: StateFlow<List<UserModel>> = _allUser


    // Login function
    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _firebaseUser.postValue(auth.currentUser)
            } else {
                _errorMessage.postValue("Something went wrong")
            }
        }
    }

    // Register function
    fun register(email: String, password: String, name: String, userImage: Uri, userName: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _firebaseUser.postValue(auth.currentUser)
                    saveImage(email, name, auth.currentUser!!.uid, userImage, userName)
            } else {
                _errorMessage.value = "Something went gone wrong"
            }
        }
    }

    // Helper function to save user image during registration
     fun saveImage(email: String, name: String, userId: String, userImage: Uri, userName: String) {
        val imageRef = storage.reference.child("user_profile/${UUID.randomUUID()}.jpg")
        val uploadTask = imageRef.putFile(userImage)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                saveData(userId, userName, name, email, uri.toString())
            }.addOnFailureListener {
            }
        }.addOnFailureListener {
        }
    }

    // Helper function to save user data in FireStore
    private fun saveData(userId: String, userName: String, name: String, email: String, profileImageUrl: String) {
        val user = UserModel(userId, userName, name, email, profileImageUrl)
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
            }
    }


    fun signOut() {
        auth.signOut()
        _firebaseUser.postValue(null)
    }


    // Add this function to your AuthViewModel
    fun getAllUsers() {
        db.collection("users").get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { document ->
                    document.toObject(UserModel::class.java)
                }
                _allUser.value = users
            }
            .addOnFailureListener { exception ->

            }
    }

    private val _updatedSuccess = MutableLiveData<Boolean>(false)
    val updatedSuccess: LiveData<Boolean> = _updatedSuccess

    fun updateProfile(bio: String, userId: String) {
        val userDocRef = db.collection("users").document(userId)
        userDocRef.update("bio", bio)
            .addOnSuccessListener {
                _updatedSuccess.value = true
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
                _updatedSuccess.value = false
            }
    }

}



