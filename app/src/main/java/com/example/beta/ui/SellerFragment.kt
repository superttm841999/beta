package com.example.beta.ui

import android.os.Bundle
import android.view.*
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
    private val model: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSellerBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        binding.txtZhengTong.text = "我们的执行长，帅气的郑彤再次欢迎您， 用户 @${model.user.value!!.username}@ 😀, 希望您有良好的体验。"

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.seller, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.sellerFoodFragment -> nav.navigate(R.id.sellerFoodFragment)
            R.id.orderListFragment -> nav.navigate(R.id.orderListFragment)
        }
        return super.onOptionsItemSelected(item)
    }

}