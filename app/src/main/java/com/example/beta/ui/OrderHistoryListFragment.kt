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
import com.example.beta.data.OrderFoodViewModel
import com.example.beta.data.OrderListViewModel
import com.example.beta.databinding.FragmentOrderHistoryListBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.OrderHistoryAdapter
import kotlinx.coroutines.launch


class OrderHistoryListFragment : Fragment() {

    private lateinit var binding: FragmentOrderHistoryListBinding
    private val nav by lazy { findNavController() }
    private val vm: OrderListViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentOrderHistoryListBinding.inflate(inflater,container,false)

        val adapter = OrderHistoryAdapter() { holder, order ->
            holder.root.setOnClickListener {
               // nav.navigate(R.id.shopFoodAdminFragment, bundleOf("id" to category.docId))
            }
        }

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        lifecycleScope.launch{
            var orders = vm.getAllAdmin(model.user.value!!.id)
            adapter.submitList(orders)

            binding.txtCount.text = "${orders.size} Order(s)"
        }

        return binding.root
    }


}