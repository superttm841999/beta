package com.example.beta.util

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.beta.R
import com.example.beta.address.AddressViewModel
import com.example.beta.address.AddressViewModelFactory
import com.example.beta.login.*
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class BaseActivity: AppCompatActivity() {
    private val userDb by lazy { Firebase.firestore.collection("Users")}
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var addressViewModel: AddressViewModel
    private val login = LoginRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity", javaClass.simpleName)
        val user = intent?.getSerializableExtra("USER_INFO")?: null
        if(user!=null){

            //track user
            loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
            loginViewModel.loginStatus.value = LoginStatus(true)
            loginViewModel.user.value = user as User

            userDb?.document(user.id).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore user", "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    var result = snapshot.data
                    Log.d("testfire", result.toString())

                    var username: String = result?.get("username") as String
                    var name: String = result?.get("name").toString()
                    var email: String = result?.get("email") as String
                    var role: Int = (result?.get("role").toString()).toIntOrNull()?:0
                    var phone: String = (result?.get("phone")?:"-1") as String
                    var gender: Int? = (result?.get("gender").toString()).toIntOrNull()?:-1
                    var status: Int? = (result?.get("status").toString()).toIntOrNull()?:0

                    var oldUser = loginViewModel.user.value as User
                    var newUser = User(user.id, username, name, email, phone, gender, role, status)

                    var image = result?.get("image")
                    image?.let{
                        if(loginViewModel.image.value != it as Blob){
                            loginViewModel.image.value = it as Blob
                        }
                    }
                    if(!compareUser(oldUser, newUser)){
                        loginViewModel.user.value = newUser
                    }
                }
            }

            loginViewModel.user.observe(this, Observer {
                if(it == null){
                    return@Observer
                }
                if(it.status != 1){
                    it.status?.let { it -> statusDialog(it) }
                }
            })

            //track address
            addressViewModel = ViewModelProvider(this, AddressViewModelFactory(user.id)).get(AddressViewModel::class.java)
        }
    }

    private fun statusDialog(status: Int){
        var message = when(status){
            0 -> getString(R.string.pending_user)
            2 -> getString(R.string.blocked_user)
            else -> getString(R.string.pending_user)
        }
        var dialog = AlertDialog.Builder(this)
            .setTitle("Account Status")
            .setMessage(message)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                rmUserSR(this)
                var intent = Intent(this, LoginActivity::class.java)
                dialogInterface.dismiss()
                startActivity(intent)
                finish()
            }).create()
        dialog.setCancelable(false)
        dialog.show()
    }
}