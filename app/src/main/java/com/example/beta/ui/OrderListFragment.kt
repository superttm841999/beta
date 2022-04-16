package com.example.beta.ui

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.databinding.FragmentOrderListBinding

class OrderListFragment : Fragment() {

    private lateinit var binding: FragmentOrderListBinding
    private val nav by lazy { findNavController() }
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentOrderListBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)



        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sellerorder, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.pendingOrderFragment -> nav.navigate(R.id.pendingOrderFragment, bundleOf("id" to id))
            R.id.inProgressOrderFragment -> nav.navigate(R.id.inProgressOrderFragment, bundleOf("id" to id))
            R.id.doneOrderFragment -> nav.navigate(R.id.doneOrderFragment, bundleOf("id" to id))
        }
        return super.onOptionsItemSelected(item)
    }

}