package com.example.beta.ui

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beta.R
import com.example.beta.data.SellerListAdminViewModel
import com.example.beta.data.SellerViewModel
import com.example.beta.databinding.FragmentShopFoodBinding
import com.example.beta.util.ShopListAdapter
import kotlinx.coroutines.launch


class ShopFoodFragment : Fragment() {

    private lateinit var binding: FragmentShopFoodBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerListAdminViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShopFoodBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        val adapter = ShopListAdapter() { holder, category ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.foodListFragment, bundleOf("id" to category.docId,"shop" to category.name))
            }
        }
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


        lifecycleScope.launch{
            var application = vm.getAll()
            adapter.submitList(application)

            binding.txtCount.text = "${application.size} Shop(s)"
        }

        binding.fabCart.setOnClickListener { nav.navigate(R.id.cartListFragment) }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.shopfood, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.cartListFragment -> nav.navigate(R.id.cartListFragment)
        }

        return super.onOptionsItemSelected(item)
    }

}