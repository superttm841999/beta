package com.example.beta.ui

import android.os.Bundle
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
import com.example.beta.databinding.FragmentFoodListBinding
import com.example.beta.util.ShopFoodAdapter
import kotlinx.coroutines.launch

class FoodListFragment : Fragment() {

    private lateinit var binding: FragmentFoodListBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerListAdminViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val shop by lazy { requireArguments().getString("shop") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentFoodListBinding.inflate(inflater,container,false)

        val adapter = ShopFoodAdapter(){ holder, food ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.foodDetailFragment, bundleOf("id" to food.id,"shop" to shop,"shopId" to id))
            }
        }
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {
            val shop = vm.get(id)!!
            binding.txtName.text = "${shop.name}"
            binding.txtAddress.text = "📍 ${shop.address}"

            val foods = vm.getFoods(id)
            adapter.submitList(foods)
            binding.txtCount.text = "${foods.size} Food(s)"
        }

        binding.fabCart.setOnClickListener { nav.navigate(R.id.shopCartFragment) }

        return binding.root
    }

}