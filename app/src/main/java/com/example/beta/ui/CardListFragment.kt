package com.example.beta.ui

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
import com.example.beta.data.CardViewModel
import com.example.beta.databinding.FragmentCardListBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.CardAdapter


class CardListFragment : Fragment() {

    private lateinit var binding: FragmentCardListBinding
    private val nav by lazy { findNavController() }
    private val vm: CardViewModel by activityViewModels()
    private val model: LoginViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentCardListBinding.inflate(inflater,container,false)

        binding.btnInsert.setOnClickListener { nav.navigate(R.id.insertCardFragment) }

       val adapter = CardAdapter() { holder, card ->
            // Item click
            holder.root.setOnClickListener {
                nav.navigate(R.id.cardDetailFragment, bundleOf("docId" to card.docId))
            }
            // Delete button click
            holder.btnDelete.setOnClickListener {
                vm.delete(card.docId)
            }
        }

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.setUserId(model.user.value!!.id)

        vm.cardList.observe(viewLifecycleOwner){ list ->
            adapter.submitList(list)
        }

        return binding.root
    }

}