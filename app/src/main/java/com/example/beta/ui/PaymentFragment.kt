package com.example.beta.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beta.R
import com.example.beta.data.CartViewModel
import com.example.beta.data.Count
import com.example.beta.data.VoucherUsed
import com.example.beta.data.VoucherUsedViewModel
import com.example.beta.databinding.FragmentPaymentBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.PaymentAdapter
import com.example.beta.util.successDialog
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat


class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private val total by lazy { requireArguments().getDouble("id") ?: 0.0 }
    private val shop by lazy { requireArguments().getString("shop") ?: ""}
    private val voucher by lazy { requireArguments().getInt("voucher") ?: 0}
    private val voucherName by lazy { requireArguments().getString("voucher name") ?: ""}
    private val voucherId by lazy { requireArguments().getString("voucherId") ?: ""}
    private val code by lazy { requireArguments().getString("code") ?: ""}
    private val vm: CartViewModel by activityViewModels()
    private val voucherUsed: VoucherUsedViewModel by activityViewModels()
    private val formatter = DecimalFormat("0.00")
    private val nav by lazy { findNavController() }
    private val model: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPaymentBinding.inflate(inflater,container,false)

        val adapter = PaymentAdapter()
        binding.rv.adapter = adapter

        vm.getShop(shop,model.user.value!!.username).observe(viewLifecycleOwner){carts ->
            adapter.submitList(carts)
        }



        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.btnPayment.setOnClickListener {
               runBlocking { submit() }
        }

        //val range = (1..5).shuffled().last()
        val range = 5
        var deliveryFee = range

        var foodDiscount = voucher

        Log.d("voucher",voucher.toString())
        binding.txtSubTotal.text = "RM " + formatter.format(total-foodDiscount)

        binding.txtVoucher.text = voucherName

        binding.txtDeliveryFee.text = "RM $deliveryFee"

        var totalPaid = (total - foodDiscount + deliveryFee) + ((total - foodDiscount + deliveryFee) * 0.06)
        binding.txtTotal.text ="RM " + formatter.format(totalPaid)

        return binding.root
    }

    private suspend fun submit(){
        if(voucherId != "No Voucher"){
            val c = voucherUsed.getCount("CountVoucherUsed")
            var count = c?.toInt()
            count = count?.plus(6000 + 1)

            var setCount = count?.minus(6000)
            var f = setCount?.let { it1 ->
                Count(
                    docId = "CountVoucherUsed",
                    count = it1
                )
            }

            val used = VoucherUsed(
                docId = count.toString(),
                username = model.user.value!!.username,
                voucherId = voucherId,
                voucherName = voucherName,
                voucherCode = code,
            )

            if(shop.isNotEmpty()){
                vm.deleteShop(shop,model.user.value!!.username)
            }

            voucherUsed.set(used)
            if (f != null) {
                voucherUsed.setCount(f)
            }
            successDialog("Make payment successfully")
            nav.navigate(R.id.homeFragment)
        }
    }

}