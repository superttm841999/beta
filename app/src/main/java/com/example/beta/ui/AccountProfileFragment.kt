package com.example.beta.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.databinding.FragmentAccountProfileBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AccountProfileFragment : Fragment() {
    private lateinit var binding: FragmentAccountProfileBinding
    private val model: LoginViewModel by activityViewModels()
    private val nav by lazy{ findNavController() }
    private val userDb by lazy { Firebase.firestore.collection("Users")}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAccountProfileBinding.inflate(inflater, container, false)

        //init data
        val username = binding.usernameTextInput
        val name = binding.nameTextInput
        val nameLayout = binding.nameTextInputLayout
        val email = binding.emailTextInput
        val emailLayout = binding.emailTextInputLayout
        val phone = binding.phoneTextInput
        val phoneLayout = binding.phoneTextInputLayout

        val user = model.user.value
        username.setText(user?.username)
        name.setText(user?.name)
        email.setText(user?.email)
        if(user?.phone!="-1"){
            phone.setText(user?.phone)
        }
        if(user?.gender!=-1){
            if(user?.gender==0){
                binding.maleRB.isChecked = true
            }else{
                binding.femaleRB.isChecked = true
            }
        }

        binding.image.setImageBitmap(model.image.value?.toBitmap())

        //validation
        var validationItem = mutableMapOf(
            "name" to true,
            "email" to true,
            "phone" to true
        )

        var validationItemMsg = mutableMapOf(
            "name" to getString(R.string.blank_name),
            "email" to getString(R.string.blank_email),
            "phone" to getString(R.string.invalid_phone)
        )

        name.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(name.text.toString()) -> {
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

        //button click

        binding.galleryBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            getGalleryImage.launch(intent)
        }

        binding.cameraBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getCameraImage.launch(intent)
        }

        binding.updateProfileBtn.setOnClickListener {
            if(checkValidation(validationItem)){
                var updateProfile = mutableMapOf<String, Any>()
                updateProfile["name"] = name.text.toString()
                updateProfile["email"] = email.text.toString()
                if(phone.text.toString().trim()!=""){
                    updateProfile["phone"] = phone.text.toString()
                }
                val gender = when{
                    binding.maleRB.isChecked -> 0
                    binding.femaleRB.isChecked -> 1
                    else -> null
                }

                gender?.let{
                    updateProfile["gender"] = it
                }

                updateProfile["image"] = binding.image.cropToBlob(300,300)

                model.user.value?.let { it1 ->
                    userDb.document(it1.id).update(updateProfile)
                        .addOnSuccessListener {
                            activity?.let { it2 -> "Update Profile Successfully".showToast(it2) }
                            nav.navigateUp()
                        }
                        .addOnFailureListener {
                            activity?.let { it2 -> "Update Failed. Please Try Again".showToast(it2) }
                        }
                }
            }else{

            }
        }

        return binding.root
    }

    private val getGalleryImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            it.data?.let{ it ->
                it.data?.let{
                    val bitmap = getBitMapFromUri(it)
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
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }else{
            val source = contentResolver?.let { ImageDecoder.createSource(it, uri) }
            source?.let{
                ImageDecoder.decodeBitmap(source)
            }
        }
        return bitmap
    }
}