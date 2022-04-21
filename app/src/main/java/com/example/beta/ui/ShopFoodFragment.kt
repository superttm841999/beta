package com.example.beta.ui

import android.app.AlertDialog
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
import java.text.SimpleDateFormat
import java.util.*


class ShopFoodFragment : Fragment() {

    private lateinit var binding: FragmentShopFoodBinding
    private val nav by lazy { findNavController() }
    private val vm: SellerListAdminViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShopFoodBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        val adapter = ShopListAdapter() { holder, shop ->


            holder.root.setOnClickListener {
                    if (holder.txtShopStatus.text == "OPEN") {
                        nav.navigate(R.id.foodListFragment, bundleOf("id" to shop.docId,"shop" to shop.name))
                    }else{
                        AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_error)
                            .setTitle("Error")
                            .setMessage("The shop is closed :(")
                            .setPositiveButton("Dismiss", null)
                            .show()
                        nav.navigate(R.id.shopFoodFragment)
                    }
            }
        }
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.sellerList.observe(viewLifecycleOwner){ list ->

            adapter.submitList(list)
            binding.txtCount.text = "${list.size} Shop(s)"

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