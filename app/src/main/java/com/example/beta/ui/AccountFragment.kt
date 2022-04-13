package com.example.beta.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.Seller
import com.example.beta.data.VoucherUsed
import com.example.beta.databinding.FragmentAccountBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.toBitmap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private val nav by lazy { findNavController() }
    private val model: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentAccountBinding.inflate(inflater,container,false)

        binding.imgUser.setImageBitmap(model.image.value?.toBitmap())
        binding.txtUsername.text = model.user.value!!.username
        binding.txtEmail.text = model.user.value!!.email

       // binding.btnSellerForm.visibility = View.INVISIBLE
        binding.btnSellerForm.setOnClickListener {

            Firebase.firestore.collection("Seller").get().addOnSuccessListener {
                    snap ->
                val list = snap.toObjects<Seller>()

                list.forEach { f->
                    if(f.username == model.user.value!!.username && f.status == 0){
                        AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_error)
                            .setTitle("Error")
                            .setMessage("Your application still in pending")
                            .setPositiveButton("Dismiss", null)
                            .show()
                        nav.navigateUp()
                    }
                }

            }


            if(model.user.value!!.role == 0){
                nav.navigate(R.id.sellerFormFragment)
            }else if(model.user.value!!.role == 1){
                Firebase.firestore.collection("Seller").get().addOnSuccessListener {
                        snap ->
                    val list = snap.toObjects<Seller>()

                    list.forEach { f->
                       if(f.username == model.user.value!!.username && f.status == 1){
                           Toast.makeText(context,"Welcome Back",Toast.LENGTH_SHORT).show()
                           nav.navigate(R.id.sellerFoodFragment, bundleOf("id" to f.docId,"shopName" to f.name))

                       }else if(f.username == model.user.value!!.username && f.status == 2){
                           AlertDialog.Builder(context)
                               .setIcon(R.drawable.ic_error)
                               .setTitle("Error")
                               .setMessage("Your shop was rejected by ${f.approvalName}, You can contact to him/hers with" +
                                       " email ${f.approvalEmail}")
                               .setPositiveButton("Dismiss", null)
                               .show()
                           return@addOnSuccessListener
                       }
                    }

                }

            }

        }

        return binding.root
    }
}