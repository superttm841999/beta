package com.example.beta.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Blob

class LoginViewModel: ViewModel() {
    var user = MutableLiveData<User>()
    var loginStatus = MutableLiveData<LoginStatus>()
    var image  = MutableLiveData<Blob>()
    private val repository: LoginRepository = LoginRepository()

    override fun onCleared() {
        super.onCleared()
        Log.d("VM", "VM has been cleared")
    }

    suspend fun login(username: String, password:String): Boolean {

        val result = repository.login(username, password)
        var loginned = result?.get("valid")?: true
        Log.d("testing", loginned.toString())

        return if(loginned as Boolean){
            var id: String = result?.get("id") as String
            var username: String = result?.get("username") as String
            var name: String = result?.get("name").toString()
            var email: String = result?.get("email") as String
            var role: Int = (result?.get("role").toString()).toIntOrNull()?:0
            var phone: String = (result?.get("phone")?:"-1") as String
            var gender: Int? = (result?.get("gender").toString()).toIntOrNull()?:-1
            var status: Int? = (result?.get("status").toString()).toIntOrNull()?:0

            user.value = User(
                id = id,
                username = username,
                name = name,
                email = email,
                role = role,
                phone = phone,
                gender = gender,
                status = status,
            )
            loginStatus.value = LoginStatus(loginned as Boolean)
            true
        }else{
            loginStatus.value = LoginStatus(loginned as Boolean)
            false
        }
    }

    suspend fun checkExistingUser(username: String): Boolean{
        return repository.checkExistingUser(username)
    }

    suspend fun signUp(
        username: String,
        name: String,
        email: String,
        password: String
    ): String {
        return repository.signUp(username, name, email, password)
    }


}