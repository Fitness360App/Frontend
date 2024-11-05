package com.example.fitness360.utils

import android.content.Context

fun saveUserUid(context: Context, uid: String) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("user_uid", uid)
    editor.apply()
}

fun getUserUid(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("user_uid", null)
}

fun clearUserUid(context: Context) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("user_uid")
    editor.apply()
}