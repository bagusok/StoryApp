package com.example.storyapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat


@SuppressLint("SimpleDateFormat")
fun formatDate(date: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val parsedDate = formatter.parse(date)
    val newFormatter = SimpleDateFormat("dd MMMM yyyy HH:mm", java.util.Locale.getDefault())
    return newFormatter.format(parsedDate)
}