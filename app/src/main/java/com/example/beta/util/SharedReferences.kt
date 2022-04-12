package com.example.beta.util

import android.content.Context
import android.content.Context.MODE_PRIVATE

fun readUserSR(context: Context): MutableMap<String, Any> {
    val prefs = context.getSharedPreferences("user", MODE_PRIVATE)
    val username = prefs.getString("username", "")
    val password = prefs.getString("password", "")
    return mutableMapOf(
        "username" to username.toString(),
        "password" to password.toString()
    )
}

fun writeUserSR(context: Context, username: String, password: String){
    val prefs = context.getSharedPreferences("user", MODE_PRIVATE)
    var editor = prefs.edit()
    editor.putString("username", username)
    editor.putString("password", password)
    editor.commit()
}

fun rmUserSR(context: Context){
    val prefs = context.getSharedPreferences("user", MODE_PRIVATE)
    var editor = prefs.edit()
    editor.clear()
    editor.apply()
}
