package com.example.beta.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beta.R
import com.example.beta.data.CartViewModel
import com.example.beta.databinding.FragmentPaymentBinding
import com.example.beta.util.PaymentAdapter
import java.text.DecimalFormat


class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private val total by lazy { requireArguments().getDouble("id") ?: 0.0 }
    private val shop by lazy { requireArguments().getString("shop") ?: ""}
    private val deliveryVoucher by lazy { requireArguments().getString("delivery_voucher") ?: ""}
    private val foodVoucher by lazy { requireArguments().getString("food_voucher") ?: ""}
    private val vm: CartViewModel by activityViewModels()
    private val formatter = DecimalFormat("0.00")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPaymentBinding.inflate(inflater,container,false)

        val adapter = PaymentAdapter()
        binding.rv.adapter = adapter

        vm.getShop(shop).observe(viewLifecycleOwner){carts ->
            adapter.submitList(carts)
        }



        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


        //val range = (1..5).shuffled().last()
        val range = 5
        var deliveryFee = range
        if(deliveryVoucher == "None"){
            //deliveryFee = deliveryFee
        }else if(deliveryVoucher == "Free Delivery"){
            deliveryFee = 0
        }else if(deliveryVoucher == "Free RM 1" && deliveryFee >= 1){
            deliveryFee -= 1
        }
        var foodDiscount = 0
        if(foodVoucher == "None"){

        }else if(foodVoucher == "RM 5"){
            foodDiscount = 5
        }else if(foodVoucher == "RM 10"){
            foodDiscount = 10
        }

        binding.txtSubTotal.text = "RM " + formatter.format(total-foodDiscount)
        binding.txtDeliveryVoucher.text = deliveryVoucher
        binding.txtFoodVoucher.text = foodVoucher

        binding.txtDeliveryFee.text = "RM $deliveryFee"

        var totalPaid = (total - foodDiscount + deliveryFee) + ((total - foodDiscount + deliveryFee) * 0.06)
        binding.txtTotal.text ="RM " + formatter.format(totalPaid)

        return binding.root
    }

}