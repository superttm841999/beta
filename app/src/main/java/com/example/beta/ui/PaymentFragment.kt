package com.example.beta.ui

import android.app.AlertDialog
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
import com.example.beta.data.*
import com.example.beta.databinding.FragmentPaymentBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.PaymentAdapter
import com.example.beta.util.successDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
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
    private val address by lazy { requireArguments().getString("address") ?: ""}
    private val vm: CartViewModel by activityViewModels()
    private val voucherUsed: VoucherUsedViewModel by activityViewModels()
    private val formatter = DecimalFormat("0.00")
    private val nav by lazy { findNavController() }
    private val model: LoginViewModel by activityViewModels()
    private val order: OrderViewModel by activityViewModels()
    private val orderFood: OrderFoodViewModel by activityViewModels()
    private val seller: SellerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPaymentBinding.inflate(inflater,container,false)

        val adapter = PaymentAdapter()
        binding.rv.adapter = adapter

        vm.getShop(shop,model.user.value!!.username).observe(viewLifecycleOwner){carts ->
            adapter.submitList(carts)
        }

        binding.txtAddress.text = "📍 ${address}"

        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))



        //val range = (1..5).shuffled().last()
        val range = 5
        var deliveryFee = range

        var foodDiscount = voucher

        Log.d("voucher",voucher.toString())
        binding.txtSubTotal.text = "RM " + formatter.format(total)

        binding.txtVoucher.text = voucherName

        binding.txtDeliveryFee.text = "RM $deliveryFee"

        var totalPaid = (total - foodDiscount + deliveryFee) + ((total - foodDiscount + deliveryFee) * 0.06)
        binding.txtTotal.text ="RM " + formatter.format(totalPaid)

        Log.d("seller",shop)

        Firebase.firestore.collection("Seller").get().addOnSuccessListener {
                snap ->
            val list = snap.toObjects<Seller>()

            list.forEach { f->
                if(f.name == shop){
                    binding.btnPayment.setOnClickListener {
                        runBlocking { submit(totalPaid,f.docId) }
                    }
                }
            }

        }


        return binding.root
    }

    private suspend fun submit(totalPaid : Double, sellerId : String){
        if(voucherId != "No Id"){
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

            voucherUsed.set(used)
            if (f != null) {
                voucherUsed.setCount(f)
            }


        }
        runBlocking { addToOrder(totalPaid,sellerId) }

    }

    private suspend fun addToOrder(totalPaid: Double, sellerId: String){
        val c = voucherUsed.getCount("CountOrder")
        var count = c?.toInt()
        count = count?.plus(7000 + 1)

        var setCount = count?.minus(7000)
        var f = setCount?.let { it1 ->
            Count(
                docId = "CountOrder",
                count = it1
            )
        }

        val orderAdd = Order(
            docId = count.toString(),
            payment = totalPaid,
            status = 0,
            userId = model.user.value!!.id,
            sellerId = sellerId,
            deliveryFee = 5,
            subTotal = total,
            tax = 6,
            voucherName = voucherName,
            voucherValue = voucher,
            address = address,
            progress = 10,
        )

        runBlocking { addToOrderFood(count!!) }

        order.set(orderAdd)
        if (f != null) {
            order.setCount(f)
        }

        if(shop.isNotEmpty()){
            vm.deleteShop(shop,model.user.value!!.username)
            vm.getShopDelete(shop)

        }

        successDialog("Make payment successfully")
        nav.navigate(R.id.homeFragment)
    }

    private suspend fun addToOrderFood(counts: Int){


        vm.getShop(shop,model.user.value!!.username).observe(viewLifecycleOwner){carts ->
            for(c1 in carts){
                val c = runBlocking { voucherUsed.getCount("CountOrderFood") }
                var count = c?.toInt()
                count = count?.plus(8000 + 1)

                var setCount = count?.minus(8000)
                var f = setCount?.let { it1 ->
                    Count(
                        docId = "CountOrderFood",
                        count = it1
                    )
                }

                val orderFoodAdd = OrderFood(
                    docId = count.toString(),
                    orderId = counts.toString(),
                    foodId = c1.id,
                    quantity = c1.count,
                )
               // Log.d("orderFoodAdd",orderFoodAdd.toString())
                orderFood.set(orderFoodAdd)
                Log.d("orderFoodAdd",orderFoodAdd.toString())
                if (f != null) {
                    orderFood.setCount(f)
                }
            }
        }

    }
}