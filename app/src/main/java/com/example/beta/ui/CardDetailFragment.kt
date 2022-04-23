package com.example.beta.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.CardViewModel
import com.example.beta.databinding.FragmentCardDetailBinding

import com.example.beta.login.LoginViewModel
import kotlinx.coroutines.runBlocking


class CardDetailFragment : Fragment() {

    private lateinit var binding: FragmentCardDetailBinding
    private val nav by lazy { findNavController() }
    private val vm: CardViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()
    private val docId by lazy { requireArguments().getString("docId") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentCardDetailBinding.inflate(inflater,container,false)

        runBlocking { load() }

        return binding.root
    }

    private suspend fun load(){
        val f = vm.get(docId)
        if(f == null){
            nav.navigateUp()
            return
        }

        binding.txtCardNo.text =f.cardNo
        binding.txtDate.text = f.date
        binding.txtCVV.text = f.cvv
        binding.txtType.text = f.type
        binding.txtName.text = f.name
        binding.txtAddress.text = f.address
    }

}