package com.example.beta.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.beta.R
import com.example.beta.databinding.FragmentForgotPasswordBinding
import com.example.beta.login.LoginRepository
import com.example.beta.util.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking


class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val login = LoginRepository()
    private val userDb by lazy { Firebase.firestore.collection("Users")}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        var validationItem = mutableMapOf(
            "username" to false
        )
        var validationItemMsg = mutableMapOf(
            "username" to getString(R.string.blank_username)
        )

        val username = binding.usernameTextInput
        val usernameLayout = binding.usernameTextInputLayout

        username.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(username.text.toString()) -> {
                        validationItem["username"] = false
                        usernameLayout.error = validationItemMsg["username"].toString()
                    }
                    runBlocking { login.checkExistingUser(username.text.toString().trim()) } -> {
                        validationItemMsg["username"] = "Please enter valid username"
                        usernameLayout.error = validationItemMsg["username"].toString()
                        validationItem["username"] = false
                    }
                    else -> {
                        usernameLayout.error = null
                        validationItem["username"] = true

                    }
                }
            }
        )

        binding.sendEmailBtn.setOnClickListener {
            val username = binding.usernameTextInput.text.toString()
            if(checkValidation(validationItem)){
                runBlocking {
                    var user = login.getEmailByUsername(username)
                    Log.d("usertesting", user.toString())

                    if(user["valid"] as Boolean){
                        binding.sendEmailBtn.isEnabled = false

                        var password = randomAlphaNumericString()
                        userDb.document(user["id"] as String).update("password", md5(password)).addOnSuccessListener {
                            var subject = "Forgot Password - Food Ordering System"
                            var content = contentHTML(user["username"] as String, password)
                            var email = user["email"].toString()
                            Log.d("emailtest", email.toString())
                            SimpleEmail()
                                .to(email)
                                .subject(subject)
                                .content(content)
                                .isHtml()
                                .send {
                                    binding.sendEmailBtn.isEnabled = true
                                    if(it){
                                        binding.sendEmailBtn.text = "Sent - Try Click Again If Not Receive"
                                    }else{
                                        binding.sendEmailBtn.text = "Send Failed - Please Try Again"
                                    }
                                }
                        }


                    }


                }
            }else{
                if(validationItem["username"] == false){
                    usernameLayout.error = validationItemMsg["username"]
                }
            }
        }


        return binding.root
    }

    private fun contentHTML(username: String, password: String): String {
        return """
            <h2>Forgot Password Reset</h2></br>
            Hi, <strong>$username</strong>,<br/>Your Account's Password have been reset to <strong>$password</strong>
            <br/>By Food Ordering System
        """.trimIndent()
    }
}