package com.example.beta.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.*
import com.example.beta.databinding.FragmentFoodDetailBinding
import com.example.beta.util.toBitmap
import java.text.DecimalFormat

class FoodDetailFragment : Fragment() {

    private lateinit var binding: FragmentFoodDetailBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerListAdminViewModel by activityViewModels()

    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val shop by lazy { requireArguments().getString("shop") ?: "" }
    private val formatter = DecimalFormat("0.00")
    private val cvm: CartViewModel by activityViewModels()
    private var price = 0.00

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentFoodDetailBinding.inflate(inflater,container,false)
        binding.edtQty.requestFocus()

        binding.btnFoodDetailBack.setOnClickListener {
            nav.navigateUp()
        }

        read()

        var count = 1

        cvm.get(id).observe(viewLifecycleOwner){f ->
            if(f == null){
                binding.edtQty.setText(count.toString())
                return@observe
            }
            count = f.count.toString().toIntOrNull()?:1
            binding.edtQty.setText(count.toString())
        }

        binding.btnAddCart.setOnClickListener {
            addCart()
        }
        return binding.root
    }

    private fun addCart() {

        val c = Cart(
            id = id,
            name = binding.txtFoodDetailName.text.toString(),
            price = price.toString().toDoubleOrNull() ?: 0.00,
            count = binding.edtQty.text.toString().toIntOrNull()?:1,
            shop_name = shop
        )
        cvm.insert(c)
        nav.navigate(R.id.cartListFragment)
    }

    private fun read() {
        val f = vm.getFoodDetail(id)
        if(f == null){
            nav.navigateUp()
            return
        }
        load(f)
    }

    private fun load(f: Food) {
        binding.txtFoodDetailName.text = f.name
        binding.txtFoodDetailDescription.text = f.description
        binding.txtFoodDetailPrice.text = "RM " + formatter.format(f.price)
        price = f.price
        binding.imgFood.setImageBitmap(f.image.toBitmap())
    }


}