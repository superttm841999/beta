package com.example.beta.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.databinding.FragmentAccountChangePasswordBinding
import com.example.beta.login.LoginActivity
import com.example.beta.login.LoginRepository
import com.example.beta.login.LoginViewModel
import com.example.beta.util.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking


class AccountChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentAccountChangePasswordBinding
    private val nav by lazy{ findNavController() }
    private val model: LoginViewModel by activityViewModels()
    private val login = LoginRepository()
    private val userDb by lazy { Firebase.firestore.collection("Users")}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? ? {
        // Inflate the layout for this fragment
        binding = FragmentAccountChangePasswordBinding.inflate(inflater, container, false)

        val pwd = binding.currentPwdTextInput
        val pwdLayout = binding.currentPwdTextInputLayout
        val newPwd = binding.newPwdTextInput
        val newPwdLayout = binding.newPwdTextInputLayout
        val confirmPwd = binding.cNewPwdTextInput
        val confirmPwdLayout = binding.cNewPwdTextInputLayout

        //validation
        var validationItem = mutableMapOf(
            "currentPwd" to false,
            "newPwd" to false,
            "confirmPwd" to false
        )

        var validationItemMsg = mutableMapOf(
            "currentPwd" to getString(R.string.blank_password),
            "newPwd" to getString(R.string.blank_password),
            "confirmPwd" to "No Same With New Password"
        )

        pwd.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(pwd.text.toString()) -> {
                        pwdLayout.error = validationItemMsg["currentPwd"].toString()
                        validationItem["currentPwd"] = false
                    }
                    runBlocking { login.checkUserPwd(model.user.value!!.username, md5(pwd.text.toString())) } -> {
                        validationItemMsg["currentPwd"] = "Please Enter Correct Password"
                        pwdLayout.error = validationItemMsg["currentPwd"].toString()
                        validationItem["currentPwd"] = false
                    }
                    else -> {
                        pwdLayout.error = null
                        validationItem["currentPwd"] = true
                    }
                }
            }
        )

        newPwd.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(newPwd.text.toString()) -> {
                        newPwdLayout.error = validationItemMsg["newPwd"].toString()
                        validationItem["newPwd"] = false
                    }
                    else -> {
                        newPwdLayout.error = null
                        validationItem["newPwd"] = true
                    }
                }
            }
        )

        confirmPwd.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(confirmPwd.text.toString()) -> {
                        confirmPwdLayout.error = validationItemMsg["confirmPwd"].toString()
                        validationItem["confirmPwd"] = false
                    }
                    (newPwd.text.toString() != confirmPwd.text.toString())->{
                        validationItemMsg["confirmPwd"] = "Not Same With New Password"
                        confirmPwdLayout.error = validationItemMsg["confirmPwd"].toString()
                        validationItem["confirmPwd"] = false
                    }
                    else -> {
                        confirmPwdLayout.error = null
                        validationItem["confirmPwd"] = true
                    }
                }
            }
        )

        binding.updatePwdBtn.setOnClickListener {
            if(checkValidation(validationItem)){
                var updateProfile = mutableMapOf<String, Any>()
                updateProfile["password"] = md5(newPwd.text.toString())

                model.user.value?.let { it1 ->
                    userDb.document(it1.id).update(updateProfile)
                        .addOnSuccessListener {
                            activity?.let { it2 -> "Update Password Successfully. Please Re-login again".showToast(it2) }
                            activity?.let { it2 -> rmUserSR(it2) }
                            var intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                        .addOnFailureListener {
                            activity?.let { it2 -> "Change Password Failed. Please Try Again".showToast(it2) }
                        }
                }
            }else{
                if(validationItem["currentPwd"] == false){
                    pwdLayout.error = validationItemMsg["currentPwd"]
                }

                if(validationItem["newPwd"] == false){
                    newPwdLayout.error = validationItemMsg["newPwd"]
                }

                if(validationItem["confirmPwd"] == false){
                    confirmPwdLayout.error = validationItemMsg["confirmPwd"]
                }
            }
        }

        return binding.root
    }
}