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
import com.example.beta.data.Count
import com.example.beta.data.Food
import com.example.beta.data.FoodViewModel
import com.example.beta.data.Seller
import com.example.beta.databinding.FragmentInsertFoodBinding
import com.example.beta.util.cropToBlob
import com.example.beta.util.errorDialog
import com.example.beta.util.successDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking

class InsertFoodFragment : Fragment() {

    private lateinit var binding: FragmentInsertFoodBinding
    private val nav by lazy { findNavController() }
    private val vm: FoodViewModel by activityViewModels()
    private val shopId by lazy { requireArguments().getString("shopId") ?: "" }
    private val shopName by lazy { requireArguments().getString("shopName") ?: "" }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgPhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentInsertFoodBinding.inflate(inflater,container,false)

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
        binding.btnSubmit.setOnClickListener { runBlocking { submit() } }

        return binding.root
    }

    private fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun reset(){
        binding.edtName.text.clear()
        binding.imgPhoto.setImageDrawable(null)
        binding.edtDescription.text.clear()
        binding.edtPrice.text.clear()
        binding.spnShop.text = shopName
        binding.spnStatus.setSelection(0)

        binding.edtName.requestFocus()
    }

    private suspend fun submit(){
        val c = vm.getCount("CountFood")
        var count = c?.toInt()
        count = count?.plus(5000 + 1)

        var setCount = count?.minus(5000)
        var cc = setCount?.let {
            Count(
                docId = "CountFood",
                count = it
            )
        }

        val f = Food(
            id = count.toString(),
            name = binding.edtName.text.toString().trim(),
            status = binding.spnStatus.selectedItem.toString(),
            image = binding.imgPhoto.cropToBlob(300,300),
            description = binding.edtDescription.text.toString(),
            price = binding.edtPrice.text.toString().toDoubleOrNull()?:0.00,
            applicationId = shopId
        )

        val err = vm.validate(f)
        if(err != ""){
            errorDialog(err)
            return
        }

        vm.set(f)
        if (cc != null) {
            vm.setCount(cc)
        }
        successDialog("Food added successfully")
        nav.navigateUp()

    }
}

