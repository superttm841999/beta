package com.example.beta.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beta.R
import com.example.beta.data.VoucherViewModel
import com.example.beta.databinding.FragmentVoucherBinding
import com.example.beta.util.VoucherAdapter


class VoucherFragment : Fragment() {

    private lateinit var binding: FragmentVoucherBinding
    private val nav by lazy { findNavController() }
    private val vm: VoucherViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentVoucherBinding.inflate(inflater,container,false)

        val adapter = VoucherAdapter(){ holder, voucher ->
            if(holder.txtDate.text != "Coming soon or expired")
            holder.root.setOnClickListener {
                AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_notifications_black_24dp)
                    .setTitle("Code")
                    .setMessage("${voucher.code}")
                    .setPositiveButton("Back", null)
                    .show()
            }
        }

        binding.rvVoucherList.adapter = adapter
        binding.rvVoucherList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


        vm.voucherList.observe(viewLifecycleOwner){ list ->
            adapter.submitList(list)

        }

        return binding.root
    }

}