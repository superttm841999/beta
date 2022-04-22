package com.example.beta.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beta.R
import com.example.beta.data.Order
import com.example.beta.data.OrderListViewModel
import com.example.beta.databinding.FragmentPendingOrderDetailBinding
import com.example.beta.util.OrderHistoryDetailAdapter
import com.example.beta.util.successDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class PendingOrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentPendingOrderDetailBinding
    private val nav by lazy { findNavController() }
    private val vm: OrderListViewModel by activityViewModels()
    private val orderId by lazy { requireArguments().getString("orderId") ?: "" }
    private val payment by lazy { requireArguments().getString("payment") ?: "" }
    private val formatter = DecimalFormat("0.00")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPendingOrderDetailBinding.inflate(inflater,container,false)

        val adapter = OrderHistoryDetailAdapter()

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        lifecycleScope.launch{
            var orders = vm.getOrderId(orderId)

            Firebase.firestore.collection("Order").get().addOnSuccessListener {
                    snap ->
                val list = snap.toObjects<Order>()
                list.forEach { l ->
                    if(l.docId ==orderId){
                        binding.txtDeliveryFee.text = "RM ${formatter.format(l.deliveryFee)}"
                        binding.txtSubtotal.text = "RM ${formatter.format(l.subTotal)}"
                        binding.txtTax.text = "${formatter.format(l.tax)} %"
                        binding.txtVoucher.text = "RM ${formatter.format(l.voucherValue)}"
                        binding.txtVoucherName.text = "(" + l.voucherName + ")"
                        binding.txtTotal.text = "RM ${formatter.format(l.payment)}"
                    }
                }
            } 

            binding.txtName.text = "# ${orderId}"
            adapter.submitList(orders)

        }

        binding.btnAccept.setOnClickListener {
            var order = mutableMapOf<String, Any>(
                "status" to 1,
                "progress" to 0,
            )

            Firebase.firestore.collection("Order").document(orderId).update(order)

            successDialog("Order accepted")
            nav.navigateUp()
        }

        binding.btnReject.setOnClickListener {
            var order = mutableMapOf<String, Any>(
                "status" to 2,
                "progress" to 2,
            )

            Firebase.firestore.collection("Order").document(orderId).update(order)

            successDialog("Order rejected")
            nav.navigateUp()
        }

        return binding.root
    }


}