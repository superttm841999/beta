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
import com.example.beta.data.OrderListViewModel
import com.example.beta.databinding.FragmentInProgressOrderBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.InProgressOrderAdapter
import kotlinx.coroutines.launch


class InProgressOrderFragment : Fragment() {

    private lateinit var binding: FragmentInProgressOrderBinding
    private val nav by lazy { findNavController() }
    private val vm: OrderListViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentInProgressOrderBinding.inflate(inflater,container,false)

        val adapter = InProgressOrderAdapter() { holder, order ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.inProgressOrderDetailFragment, bundleOf("orderId" to order.docId,"total" to order.payment))
            }
        }

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        vm.setSellerId(id,1,1)

        vm.orderList.observe(viewLifecycleOwner){ list ->

            adapter.submitList(list)
            binding.txtCount.text = "${list.size} record(s)"

        }


        return binding.root
    }


}