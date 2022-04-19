package com.example.beta.address

import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beta.R
import com.example.beta.databinding.FragmentAddressListBinding


class AddressListFragment : Fragment() {
    private lateinit var binding: FragmentAddressListBinding
    private val model: AddressViewModel by activityViewModels()
    private val nav by lazy { findNavController() }
    private var currentLocation: Location? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddressListBinding.inflate(inflater, container, false)
        binding.insertAddressBtn.setOnClickListener {
           nav.navigate(R.id.addressAddFragment3)
        }

        val adapter = AddressAdapter() { holder, address ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.addressUpdateFragment2, bundleOf("id" to address.id) )
            }
        }
        val layoutManager = LinearLayoutManager(activity)
        binding.addressRV.adapter = adapter
        binding.addressRV.layoutManager = layoutManager
        binding.addressRV.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )


        model.addresses.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

}
