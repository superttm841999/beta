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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.Category
import com.example.beta.data.Count
import com.example.beta.data.Seller
import com.example.beta.data.SellerViewModel
import com.example.beta.databinding.FragmentSellerFormBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.cropToBlob
import com.example.beta.util.errorDialog
import com.example.beta.util.successDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking

class SellerFormFragment : Fragment() {

    private lateinit var binding:FragmentSellerFormBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgPhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSellerFormBinding.inflate(inflater,container,false)

        var name = ArrayList<String>()

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
        Log.d("check",name.toString())

        val arrayAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,name)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnCat.adapter = arrayAdapter

        binding.edtUsername.text = model.user.value!!.username
        reset()
        binding.imgPhoto.setOnClickListener { select() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { runBlocking { submit() } }
        binding.btnBack.setOnClickListener { nav.navigate(R.id.accountFragment) }

        return binding.root
    }

    private fun reset() {
        binding.edtName.text.clear()
        binding.edtAddress.text.clear()
        binding.imgPhoto.setImageDrawable(null)
        binding.spnCat.setSelection(0)
        binding.edtName.requestFocus()
    }

    private fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private suspend fun submit() {
        val c = vm.getCount("CountApprovalForm")
        var count = c?.toInt()
        count = count?.plus(1000 + 1)

        Log.d("count",count.toString())

        val s = Seller(
            docId = count.toString(),
            name = binding.edtName.text.toString().trim(),
            username = model.user.value!!.username,
            status = 0,
            logo = binding.imgPhoto.cropToBlob(300,300),
            category = binding.spnCat.selectedItem.toString(),
            address = binding.edtAddress.text.toString().trim(),
            userId = model.user.value!!.id
        )

        val err = vm.validate(s)
        if(err != ""){
            errorDialog(err)
            return
        }
        var setCount = count?.minus(1000)
        var f = setCount?.let {
            Count(
                docId = "CountApprovalForm",
                count = it
            )
        }
        vm.set(s)
        if (f != null) {
            vm.setCount(f)
        }
        successDialog("Application Submitted")
        nav.navigateUp()

    }


}