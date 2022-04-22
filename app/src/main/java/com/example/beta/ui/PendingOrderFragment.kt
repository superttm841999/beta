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
import com.example.beta.data.OrderListViewModel
import com.example.beta.databinding.FragmentPendingOrderBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.PendingOrderAdapter
import kotlinx.coroutines.launch


class PendingOrderFragment : Fragment() {

    private lateinit var binding: FragmentPendingOrderBinding
    private val nav by lazy { findNavController() }
    private val vm: OrderListViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPendingOrderBinding.inflate(inflater,container,false)

        val adapter = PendingOrderAdapter() { holder, order ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.pendingOrderDetailFragment, bundleOf("orderId" to order.docId,"total" to order.payment))
            }
        }

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        vm.setSellerId(id,10,0)

        vm.orderList.observe(viewLifecycleOwner){ list ->

                    adapter.submitList(list)
                    binding.txtCount.text = "${list.size} record(s)"

        }


        return binding.root
    }


}