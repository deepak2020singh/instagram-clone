package com.example.instagramclone.utlis

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: String): String {
    // Convert the timestamp string to Long
    val timestampLong = timestamp.toLong()

    // Create a Date object from the timestamp in milliseconds
    val date = Date(timestampLong)

    // Create a SimpleDateFormat to format the Date object into a readable string (year, month, hour, minute)
    val dateFormat = SimpleDateFormat("HH:mm a", Locale.getDefault()) // Format: "2024-11-25 15:30"

    // Return the formatted date string
    return dateFormat.format(date)
}

