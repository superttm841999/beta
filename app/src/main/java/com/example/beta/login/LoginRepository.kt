package com.example.beta.login

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class LoginRepository() {

    private val db by lazy { Firebase.firestore }

    suspend fun login(username: String, password: String): MutableMap<String, Any>? {
        val collection = "Users"
        val result = db.collection(collection)
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .await()
        return if(result.documents.size == 0){
            mutableMapOf<String, Any>("valid" to false)
        }else {
            var id: String = result.documents[0].id.toString()
            var data =  result.documents[0].data?.toMutableMap()
            data?.set("id", id)
            data
        }
    }

    suspend fun checkExistingUser(username: String): Boolean{
        val collection = "Users"
        val result = db.collection(collection)
            .whereEqualTo("username", username)
            .get()
            .await()
        //if size == 0 mean don't has
        //true mean can sign up a new
        return result.documents.size == 0
    }

    suspend fun checkUserPwd(username: String, password: String): Boolean{
        val collection = "Users"
        val result = db.collection(collection)
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .await()
        //if size == 0 mean don't has
        //true mean can sign up a new
        return result.documents.size == 0
    }

    suspend fun getEmailByUsername(username: String): MutableMap<String?, Any?> {
        val collection = "Users"
        val result = db.collection(collection)
            .whereEqualTo("username", username)
            .get()
            .await()

        val id = result?.documents?.get(0)?.id
        val username = result?.documents?.get(0)?.get("username")
        val email = result?.documents?.get(0)?.get("email")
        val valid = result.documents.size != 0

        return mutableMapOf(
            "valid" to valid,
            "id" to id,
            "username" to username,
            "email" to email
        )
    }

    suspend fun signUp(username: String, name: String, email: String, password: String): String {
        val collection = "Users"
        var user = mutableMapOf<String, Any>(
            "username" to username,
            "name" to name,
            "email" to email,
            "password" to password,
            "role" to 0,
            "status" to 1
        )

        var result = db.collection(collection).add(user).await()
        return result.id
    }

    suspend fun getImage(id: String): Any? {
        val collection = "Users"
        val result = db.collection(collection).document(id).get().await()
        return result.data?.get("image")
    }

    fun logout(){


    }
}