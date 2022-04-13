package com.example.beta.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beta.R
import com.example.beta.data.SellerListAdminViewModel
import com.example.beta.databinding.FragmentSellerFoodBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.ShopFoodAdapter
import kotlinx.coroutines.launch


class SellerFoodFragment : Fragment() {

    private lateinit var binding:FragmentSellerFoodBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerListAdminViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val shopName by lazy { requireArguments().getString("shopName") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSellerFoodBinding.inflate(inflater,container,false)


        binding.btnInsert.setOnClickListener {
            nav.navigate(R.id.insertFoodFragment, bundleOf("shopId" to id,"shopName" to shopName))
        }

        val adapter = ShopFoodAdapter(){ holder, food ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.updateFoodFragment, bundleOf("id" to food.id, "shopId" to id))
            }
        }

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {
            val category = vm.get(id)!!
            binding.txtName.text = "${category.name}"

            val foods = vm.getFoodsAdmin(id)
            adapter.submitList(foods)
            binding.txtCount.text = "${foods.size} Food(s)"
        }

        return binding.root
    }


}