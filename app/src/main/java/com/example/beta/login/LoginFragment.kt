package com.example.beta.login

import  android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.beta.MainActivity
import com.example.beta.R
import com.example.beta.databinding.FragmentLoginBinding
import com.example.beta.util.*
import kotlinx.coroutines.*

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val nav by lazy{ findNavController() }
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var validationItem = mutableMapOf(
            "username" to false,
            "password" to false,
        )
        var validationItemMsg = mutableMapOf(
            "username" to getString(R.string.blank_username),
            "password" to getString(R.string.blank_password),
        )

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)


        var rmbMe = activity?.let { checkRmbMe(it) }
        Log.d("rf", rmbMe.toString())

        if(rmbMe?.get("rmbMe") as Boolean){
            var valid = false
            runBlocking {
                valid = loginViewModel.login(rmbMe?.get("username").toString(), rmbMe?.get("password").toString())
            }
            if(!valid){
                activity?.let { it -> "Password already changed. Please login again.".showToast(it) }
            }else{
                //activity?.let { it -> "Login Successfully".showToast(it) }
                var user = loginViewModel.user.value

                user?.let { it->
                    if (checkUserStatus(it.status)){
                        activity?.let { it -> "Login Successfully".showToast(it) }
                        routeActivity(it)
                    }else {
                        activity?.let { it2 -> userStatusMessage(it2, it.status).showToast(it2) }
                    }
                }
            }
        }
        //validation

        val usernameLayout = binding.usernameTextInputLayout
        val username = binding.usernameTextInput

        username.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(username.text.toString()) -> {
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

        val pwdLayout = binding.pwdTextInputLayout
        val pwd = binding.pwdTextInput

        pwd.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(pwd.text.toString()) -> {
                        pwdLayout.error = validationItemMsg["password"].toString()
                        validationItem["password"] = false
                    }
                    else -> {
                        pwdLayout.error = null
                        validationItem["password"] = true

                    }
                }
            }
        )

        binding.registerBtn.setOnClickListener {
            nav.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginBtn.setOnClickListener {

            var username = binding.usernameTextInput.text.toString().trim()
            var password = binding.pwdTextInput.text.toString()
            var rmbLogin = binding.rmbLoginChk.isChecked

            Log.d("haha", md5(password))

            if(!checkValidation(validationItem)){
                if(!validationItem["username"]!!){
                    usernameLayout.error = validationItemMsg["username"].toString()
                }

                if(!validationItem["password"]!!){
                    pwdLayout.error = validationItemMsg["password"].toString()
                }
                return@setOnClickListener
            }

            var valid = false
            runBlocking {
                valid = loginViewModel.login(username, md5(password))
            }

            loginViewModel.loginStatus.observe(viewLifecycleOwner,
                Observer {
                    if(it == null){
                        return@Observer
                    }
                    if(it.loggedIn){
                        if(valid && rmbLogin){
                            //if login successfully and click remember me
                            //store into shared references
                            activity?.let { it -> writeUserSR(it, username, md5(password)) }
                        }
                        var user = loginViewModel.user.value

                        user?.let { it->
                            if (checkUserStatus(it.status)){
                                activity?.let { it2 -> userStatusMessage(it2, it.status).showToast(it2) }
                                routeActivity(it)
                            }else {
                                activity?.let { it2 -> userStatusMessage(it2, it.status).showToast(it2) }
                            }
                        }
                    }else{
                        Toast.makeText(activity, "Username or Password is wrong. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        return binding.root
    }

    private fun checkRmbMe(context: Context): MutableMap<String, Any>{
        var user = readUserSR(context)
        return if(user["username"] != "" && user["password"] != ""){
            mutableMapOf(
                "username" to user["username"].toString(),
                "password" to user["password"].toString(),
                "rmbMe" to true
            )
        }else{
            mutableMapOf(
                "rmbMe" to false
            )
        }
    }

    private fun routeActivity(value: User){
        if(value.role == 0 || value.role == 1){
            var intent = Intent(activity, MainActivity::class.java).apply{
                putExtra("USER_INFO", value)
            }
            startActivity(intent)
            activity?.finish()
        }
    }
}