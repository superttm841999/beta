package com.example.beta.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.Category
import com.example.beta.data.Count
import com.example.beta.data.Seller
import com.example.beta.data.SellerViewModel
import com.example.beta.databinding.FragmentProfileFoodBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.cropToBlob
import com.example.beta.util.errorDialog
import com.example.beta.util.successDialog
import com.example.beta.util.toBitmap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking


class ProfileFoodFragment : Fragment() {

    private lateinit var binding:FragmentProfileFoodBinding
    private val model: LoginViewModel by activityViewModels()
    private val vm: SellerViewModel by activityViewModels()
    private val nav by lazy { findNavController() }
    private var name = ArrayList<String>()
    private val id by lazy { requireArguments().getString("id") ?: "" }


    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgPhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentProfileFoodBinding.inflate(inflater,container,false)




        Firebase.firestore
            .collection("Category")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.toObjects<Category>()
                var result = ""

                list.forEach{ f->
                    if(f.status == "Published"){
                        name.add(f.name)
                    }
                    result += f.name
                }
            }
        name.add("--SELECT--")

        val arrayAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,name)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnCat.adapter = arrayAdapter

        binding.edtUsername.text = model.user.value!!.username

        reset()
        binding.imgPhoto.setOnClickListener { select() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { runBlocking { submit() } }



        return binding.root
    }

    private  fun reset() {
        Firebase.firestore
            .collection("Seller")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.toObjects<Seller>()
                var count = 1

                list.forEach{ f->
                    if(f.docId == id){
                        binding.edtName.setText(f.name)
                        binding.edtAddress.setText(f.address)
                        binding.edtOpen.setText(f.open)
                        binding.edtClose.setText(f.close)
                        binding.imgPhoto.setImageBitmap(f.logo.toBitmap())
                        setCategory(f.category)
                        binding.edtName.requestFocus()
                    }
                }
            }
    }

    private fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun setCategory(cat:String){
        Firebase.firestore
            .collection("Category")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.toObjects<Category>()
                var count = 1

                Log.d("caofeng1",name.toString())
                list.forEach{ f->
                   if(f.status == "Published"){
                       if(f.name == cat){
                           binding.spnCat.setSelection(count)
                           Log.d("caofeng",count.toString())
                       }
                       Log.d("caofeng",count.toString())
                       count++
                   }


                }
            }
    }

    private suspend fun submit() {
        val s = Seller(
            docId = id,
            name = binding.edtName.text.toString().trim(),
            logo = binding.imgPhoto.cropToBlob(300,300),
            category = binding.spnCat.selectedItem.toString(),
            address = binding.edtAddress.text.toString().trim(),

            )

        var shop = mutableMapOf<String, Any>(
            "name" to binding.edtName.text.toString().trim(),
            "logo" to binding.imgPhoto.cropToBlob(300,300),
            "category" to binding.spnCat.selectedItem.toString(),
            "address" to binding.edtAddress.text.toString().trim(),
            "open" to binding.edtOpen.text.toString().trim(),
            "close" to binding.edtClose.text.toString().trim(),
        )

        val err = vm.validate(s,false)
        if(err != ""){
            errorDialog(err)
            return
        }

        Firebase.firestore.collection("Seller").document(id).update(shop)

        successDialog("Shop Profile Updated")
        nav.navigate(R.id.sellerFragment,bundleOf("id" to id))

    }
}