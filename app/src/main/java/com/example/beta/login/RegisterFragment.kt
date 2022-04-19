package com.example.beta.login

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.databinding.FragmentRegisterBinding
import com.example.beta.util.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.runBlocking

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val nav by lazy{ findNavController() }
    private val model: LoginViewModel by activityViewModels()
    private val login = LoginRepository()
    private val userDb by lazy { Firebase.firestore.collection("Users")}
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.image.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val username = binding.usernameTextInput
        val usernameLayout = binding.usernameTextInputLayout
        val name = binding.nameTextInput
        val nameLayout = binding.nameTextInputLayout
        val email = binding.emailTextInput
        val emailLayout = binding.emailTextInputLayout
        val newPwd = binding.newPwdTextInput
        val newPwdLayout = binding.newPwdTextInputLayout
        val confirmPwd = binding.cNewPwdTextInput
        val confirmPwdLayout = binding.cNewPwdTextInputLayout
        val phone = binding.phoneTextInput
        val phoneLayout = binding.phoneTextInputLayout

        //validation
        var validationItem = mutableMapOf(
            "username" to false,
            "name" to false,
            "email" to false,
            "phone" to true,
            "newPwd" to false,
            "confirmPwd" to false
        )
        var validationItemMsg = mutableMapOf(
            "username" to getString(R.string.blank_username),
            "name" to getString(R.string.blank_name),
            "email" to getString(R.string.blank_email),
            "phone" to getString(R.string.invalid_phone),
            "newPwd" to getString(R.string.blank_password),
            "confirmPwd" to getString(R.string.blank_password)
        )

        var directClick = true

        username.addTextChangedListener(
            afterTextChanged = {
                directClick = false
                when{
                    isEmptyString(username.text.toString()) -> {
                        validationItem["username"] = false
                        usernameLayout.error = validationItemMsg["username"].toString()
                    }
                    runBlocking { !login.checkExistingUser(username.text.toString().trim()) } -> {
                        validationItemMsg["username"] = getString(R.string.duplicated_username)
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

        name.addTextChangedListener(
            afterTextChanged = {
                directClick = false
                when{
                    isEmptyString(name.text.toString())-> {
                        nameLayout.error = validationItemMsg["name"].toString()
                        validationItem["name"] = false
                    }
                    else -> {
                        nameLayout.error = null
                        validationItem["name"] = true
                    }
                }
            }
        )

        email.addTextChangedListener(
            afterTextChanged = {
                directClick = false
                when{
                    isEmptyString(email.text.toString())-> {
                        emailLayout.error = validationItemMsg["email"].toString()
                        validationItem["email"]=false
                    }
                    (!validateEmail(email.text.toString().trim())) ->{
                        validationItemMsg["email"] = getString(R.string.invalid_email)
                        emailLayout.error = validationItemMsg["email"].toString()
                        validationItem["email"] = false
                    }
                    else -> {
                        emailLayout.error = null
                        validationItem["email"] = true
                    }
                }
            }
        )

        var formatPhoneTime = 0
        phone.addTextChangedListener (
            afterTextChanged = {
                when{
                    (!isEmptyString(phone.text.toString()) && formatPhoneTime == 0)-> {
                        if(!validateMYPhone(phone.text.toString())){
                            phoneLayout.error = validationItemMsg["phone"]
                            validationItem["phone"] = false
                            formatPhoneTime = 0
                        }
                        if(validateMYPhone(phone.text.toString())){
                            phoneLayout.error = null
                            validationItem["phone"] = true
                            formatPhoneTime+=1
                            phone.setText(formatMYPhone(phone.text.toString()))
                        }
                    }
                    (!isEmptyString(phone.text.toString()) && formatPhoneTime == 1)-> {
                        if(!validateMYPhone(phone.text.toString())){
                            phoneLayout.error = validationItemMsg["phone"]
                            formatPhoneTime = 0
                            validationItem["phone"] = false
                        }
                    }
                    else -> {
                        phoneLayout.error = null
                        validationItem["phone"] = true
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

        binding.galleryBtn.setOnClickListener {
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "image/*"
//            getGalleryImage.launch(intent)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            launcher.launch(intent)
        }

        binding.cameraBtn.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.CAMERA)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        "Please give permission to open your camera",
                        "OK",
                        "Cancel"
                    )
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
//                      activity?.let { it -> "Permission granted".showToast(it) }
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        getCameraImage.launch(intent)
                    } else {
                        activity?.let { it -> "Disable to open camera because no permission".showToast(it) }
                    }
                }
        }

        var signUpBtn = binding.signUpBtn

        signUpBtn.setOnClickListener {
            if(checkValidation(validationItem)){
                var newUser = mutableMapOf<String, Any>()
                newUser["username"] = username.text.toString()
                newUser["name"] = name.text.toString()
                newUser["email"] = email.text.toString()
                newUser["password"] = md5(newPwd.text.toString())
                if(phone.text.toString().trim()!=""){
                    newUser["phone"] = phone.text.toString()
                }
                val gender = when{
                    binding.maleRB.isChecked -> 0
                    binding.femaleRB.isChecked -> 1
                    else -> null
                }

                gender?.let{
                    newUser["gender"] = it
                }

                newUser["status"] = 1
                newUser["role"] = 0
                newUser["image"] = binding.image.cropToBlob(300,300)

                userDb.add(newUser)
                    .addOnSuccessListener {
                        activity?.let { it2 -> "Sign Up Successfully".showToast(it2) }
                        nav.navigateUp()


                    }
                    .addOnFailureListener {
                        activity?.let { it2 -> "Sign Up Failed. Please Try Again".showToast(it2) }
                    }


            }else{
                if(validationItem["username"] == false){
                    usernameLayout.error = validationItemMsg["username"]
                }

                if(validationItem["name"] == false){
                    nameLayout.error = validationItemMsg["name"]
                }

                if(validationItem["email"] == false){
                    emailLayout.error = validationItemMsg["email"]
                }

                if(validationItem["phone"] == false){
                    phoneLayout.error = validationItemMsg["phone"]
                }

                if(validationItem["newPwd"] == false){
                    newPwd.error = validationItemMsg["newPwd"]
                }

                if(validationItem["confirmPwd"] == false){
                    confirmPwd.error = validationItemMsg["confirmPwd"]
                }
            }
        }

        return binding.root
    }

    private val getGalleryImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            it.data?.let{ it ->
                it.data?.let{
                    val bitmap = getBitMapFromUri(it)
                    Log.d("haha", bitmap.toString())
                    binding.image.setImageBitmap(bitmap)
                }
            }
        }
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            it.data?.let{ it ->
                val bitmap = it.extras?.get("data") as Bitmap
                binding.image.setImageBitmap(bitmap)
            }
        }
    }

    private fun getBitMapFromUri(uri: Uri): Bitmap? {
        val contentResolver: ContentResolver? = activity?.contentResolver
        var bitmap: Bitmap? = null
        bitmap = if(Build.VERSION.SDK_INT < 28){
            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        }else{
            val source = contentResolver?.let { ImageDecoder.createSource(it, uri) }
            source?.let{
                ImageDecoder.decodeBitmap(source)
            }
        }
        return bitmap
    }
}