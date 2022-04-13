package com.example.beta.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.Food
import com.example.beta.data.FoodViewModel
import com.example.beta.data.Seller
import com.example.beta.databinding.FragmentUpdateFoodBinding
import com.example.beta.util.cropToBlob
import com.example.beta.util.errorDialog
import com.example.beta.util.successDialog
import com.example.beta.util.toBitmap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class UpdateFoodFragment : Fragment() {
    private lateinit var binding: FragmentUpdateFoodBinding
    private val nav by lazy { findNavController() }
    private val vm: FoodViewModel by activityViewModels()

    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val shopId by lazy { requireArguments().getString("shopId") ?: "" }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgPhoto.setImageURI(it.data?.data)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUpdateFoodBinding.inflate(inflater,container,false)

        val spinner: Spinner = binding.spnStatus

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cat_Status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        reset()
        binding.imgPhoto.setOnClickListener { select() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { submit() }

        return binding.root
    }

    private fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun reset(){
        val f = vm.get(id)
        if(f == null){
            nav.navigateUp()
            return
        }
        load(f)
    }

    private fun load(f: Food) {
        binding.txtId.text = f.id
        binding.edtName.setText(f.name)
        binding.edtDescription.setText(f.description)
        binding.edtPrice.setText(f.price.toString())
        getApplicationName(f.applicationId)
        setStatus(f.status)

        binding.imgPhoto.setImageBitmap(f.image.toBitmap())

        binding.edtName.requestFocus()
    }

    private fun getApplicationName(id : String){

        var ids = id

        Firebase.firestore
            .collection("Seller")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.toObjects<Seller>()
                list.forEach{ f->
                    if(f.docId == ids){
                        binding.txtShop.text = f.name
                    }

                }
            }
    }

    private fun setStatus(status: String) {
        when (status) {
            "Published" -> binding.spnStatus.setSelection(1)
            "Drafted" -> binding.spnStatus.setSelection(0)
        }
    }

    private fun submit(){
        val f = Food(
            id = binding.txtId.text.toString(),
            name = binding.edtName.text.toString().trim(),
            status = binding.spnStatus.selectedItem.toString(),
            image = binding.imgPhoto.cropToBlob(300,300),
            description = binding.edtDescription.text.toString(),
            price = binding.edtPrice.text.toString().toDoubleOrNull()?:0.00,
            applicationId = shopId
        )

        val err = vm.validate(f,false)
        if(err != ""){
            errorDialog(err)
            return
        }

        vm.set(f)
        successDialog("Food updated successfully")
        nav.navigateUp()
    }

}