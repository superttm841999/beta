package com.example.beta.ui

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.databinding.FragmentSellerBinding
import com.example.beta.login.LoginViewModel

class SellerFragment : Fragment() {

    private lateinit var binding: FragmentSellerBinding
    private val nav by lazy { findNavController() }
    private val shopName by lazy { requireArguments().getString("shopName") ?: "" }
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val model: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSellerBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        binding.txtZhengTong.text = "Welcome Back, user ${model.user.value!!.username}"

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.seller, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.sellerFoodFragment -> nav.navigate(R.id.sellerFoodFragment,bundleOf("shopName" to shopName, "id" to id))
            R.id.orderListFragment -> nav.navigate(R.id.orderListFragment, bundleOf("id" to id))
            R.id.profileFoodFragment -> nav.navigate(R.id.profileFoodFragment, bundleOf("id" to id))
        }
        return super.onOptionsItemSelected(item)
    }

}