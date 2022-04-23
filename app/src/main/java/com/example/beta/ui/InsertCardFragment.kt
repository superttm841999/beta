package com.example.beta.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.Card
import com.example.beta.data.CardViewModel
import com.example.beta.data.Count
import com.example.beta.databinding.FragmentInsertCardBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.errorDialog
import com.example.beta.util.successDialog
import kotlinx.coroutines.runBlocking
import kotlin.reflect.typeOf


class InsertCardFragment : Fragment() {

    private lateinit var binding: FragmentInsertCardBinding
    private val nav by lazy { findNavController() }
    private val vm: CardViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentInsertCardBinding.inflate(inflater,container,false)

        val spinner: Spinner = binding.spnType

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        reset()

        binding.edtCardNo.requestFocus()

        binding.btnSubmit.setOnClickListener {
            runBlocking { submit() }
        }

        return binding.root
    }

    private suspend fun submit(){
        val c = vm.getCount("CountCard")
        var count = c?.toInt()
        count = count?.plus(8000 + 1)

        var setCount = count?.minus(8000)
        var f = setCount?.let {
            Count(
                docId = "CountCard",
                count = it
            )
        }


        val ca = Card(
            docId = count.toString(),
            cardNo = binding.edtCardNo.text.toString(),
            date = binding.edtDate.text.toString(),
            cvv = binding.edtCVV.text.toString(),
            type =  binding.spnType.selectedItem.toString(),
            name = binding.edtName.text.toString().trim(),
            address = binding.edtAddress.text.toString(),
            userId = model.user.value!!.id
        )

        val err = vm.validate(ca)
        if(err != ""){
            errorDialog(err)
            return
        }

        vm.set(ca)
        if (f != null) {
            vm.setCount(f)
        }
        successDialog("Card added successfully")
        nav.navigateUp()
    }

    private fun reset() {
        binding.edtName.text.clear()
        binding.spnType.setSelection(0)
        binding.edtCardNo.text.clear()
        binding.edtAddress.text.clear()
        binding.edtCVV.text.clear()
        binding.edtDate.text.clear()
        binding.edtName.requestFocus()
    }

}