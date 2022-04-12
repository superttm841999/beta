package com.example.beta.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beta.R
import com.example.beta.data.CartViewModel
import com.example.beta.data.VoucherViewModel
import com.example.beta.databinding.FragmentCartListBinding
import com.example.beta.util.CartAdapter
import com.example.beta.util.errorDialog
import java.text.DecimalFormat


class CartListFragment : Fragment() {

    private lateinit var binding:FragmentCartListBinding
    private val nav by lazy { findNavController() }
    private val vm: CartViewModel by activityViewModels()
    private val vc: VoucherViewModel by activityViewModels()
    private val formatter = DecimalFormat("0.00")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCartListBinding.inflate(inflater, container, false)

        var shop_name = ArrayList<String>()
        shop_name.add("--SELECT--")

        vm.getAll().observe(viewLifecycleOwner){shop ->
            for(s in shop){
                shop_name.add(s.shop_name)
            }
            val arrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,shop_name.distinct())
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnShop.adapter = arrayAdapter
            Log.d("checkb",shop_name.distinct().toString())

        }



        val adapter = CartAdapter() { holder, f ->
            // holder.root.setOnClickListener      { nav.navigate(R.id.detailFragment, bundleOf("id" to f.id)) }
            holder.btnEdit.setOnClickListener   { nav.navigate(R.id.foodDetailFragment, bundleOf("id" to f.id,"shop" to f.shop_name) ) }
            holder.btnDelete.setOnClickListener { vm.delete(f)
                nav.navigate(R.id.cartListFragment)
            }
        }

        binding.rv.adapter = adapter
        binding.btnRefresh.setOnClickListener {
            nav.navigate(R.id.cartListFragment)
        }
        binding.btnChange.setOnClickListener {
            if(binding.spnShop.selectedItem.toString() == "--SELECT--"){
                nav.navigate(R.id.cartListFragment)
            }
            vm.getShop(binding.spnShop.selectedItem.toString()).observe(viewLifecycleOwner){carts ->
                adapter.submitList(carts)
            }
        }

        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.btnBack.setOnClickListener {
            nav.navigate(R.id.shopFoodFragment)
        }
        binding.btnDeleteAll.setOnClickListener { vm.deleteAll() }

        var total = 0.00
        binding.btnPay.setOnClickListener {
            vm.getShop(binding.spnShop.selectedItem.toString()).observe(viewLifecycleOwner){carts ->
                for(c in carts){
                    total += (c.price * c.count)
                }

                val vvv = vc.get(binding.edtVoucher.text.toString())
                val err = vvv?.let { it1 -> vc.validate(it1) }
                if(err != ""){
                    if(binding.edtVoucher.text.toString() !=null){
                        if(binding.spnShop.selectedItem.toString()!= "--SELECT--"){
                            nav.navigate(R.id.paymentFragment,
                                bundleOf("id" to total,
                                    "shop" to binding.spnShop.selectedItem.toString(),
                                    "voucher" to vvv?.value,
                                    "voucher name" to vvv?.name,
                                )
                            )
                        }
                    }else{
                        if(binding.spnShop.selectedItem.toString()!= "--SELECT--"){
                            nav.navigate(R.id.paymentFragment,
                                bundleOf("id" to total,
                                    "shop" to binding.spnShop.selectedItem.toString(),
                                    "voucher" to 0,
                                    "voucher name" to "",
                                )
                            )
                        }
                    }

                }else{
                    err?.let { it1 -> errorDialog(it1) }
                    return@observe
                }


//                binding.txtCount.text = formatter.format(total)
                Log.d("lol",total.toString())
            }
        }

        return binding.root
    }

}