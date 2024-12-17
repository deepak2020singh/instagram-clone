package com.example.instagramclone.utlis

import android.content.Context.MODE_PRIVATE
import android.content.Context

object SharPref {
    fun storeData(userName: String, email: String, bio: String, imageUrl: String, context: Context){
        val sharedPref = context.getSharedPreferences("myPref", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("userName", userName)
        editor.putString("email", email)
        editor.putString("bio", bio)
        editor.putString("imageUrl", imageUrl)
        editor.apply()
    }
    fun getUserName(context: Context): String{
        val sharedPref = context.getSharedPreferences("myPref", MODE_PRIVATE)
        return sharedPref.getString("userName", "")!!
    }

    fun getEmail(context: Context): String{
        val sharedPref = context.getSharedPreferences("myPref", MODE_PRIVATE)
        return sharedPref.getString("email", "")!!
    }

    fun getBio(context: Context): String{
        val sharedPref = context.getSharedPreferences("myPref", MODE_PRIVATE)
        return sharedPref.getString("bio", "")!!
    }

    fun getImageUrl(context: Context): String{
        val sharedPref = context.getSharedPreferences("myPref", MODE_PRIVATE)
        return sharedPref.getString("imageUrl", "")!!
    }

}