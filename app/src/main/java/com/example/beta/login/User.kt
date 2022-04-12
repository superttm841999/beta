package com.example.beta.login

import com.google.firebase.firestore.Blob
import java.io.Serializable

data class User(
    var id: String,
    var username: String,
    var name: String,
    var email: String,
    var phone: String?,
    var gender: Int?,
    var role: Int,
    var status: Int?,
):Serializable
