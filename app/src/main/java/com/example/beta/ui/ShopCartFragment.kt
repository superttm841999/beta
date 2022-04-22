package com.example.beta.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.CartViewModel
import com.example.beta.data.SellerListAdminViewModel
import com.example.beta.databinding.FragmentShopCartBinding
import com.example.beta.util.CartAdapter
import com.example.beta.util.ShopCartAdapter


class ShopCartFragment : Fragment() {

    private lateinit var binding: FragmentShopCartBinding
    private val nav by lazy { findNavController() }
    private val vm: CartViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentShopCartBinding.inflate(inflater,container,false)

        val adapter = ShopCartAdapter() { holder, f ->
             holder.root.setOnClickListener   {
         nav.navigate(R.id.cartListFragment, bundleOf("shop_name" to f.name))
             }
        }

        binding.rv.adapter = adapter
        vm.getShopAll().observe(viewLifecycleOwner){carts ->
            adapter.submitList(carts)
        }

        return binding.root
    }

}