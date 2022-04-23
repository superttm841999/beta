package com.example.beta.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.data.*
import com.example.beta.databinding.FragmentPaymentMethodBinding
import com.example.beta.login.LoginViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class PaymentMethodFragment : Fragment() {

    private lateinit var binding: FragmentPaymentMethodBinding
    private val total by lazy { requireArguments().getDouble("id") ?: 0.0 }
    private val shop by lazy { requireArguments().getString("shop") ?: ""}
    private val voucher by lazy { requireArguments().getInt("voucher") ?: 0}
    private val voucherName by lazy { requireArguments().getString("voucher name") ?: ""}
    private val voucherId by lazy { requireArguments().getString("voucherId") ?: ""}
    private val code by lazy { requireArguments().getString("code") ?: ""}
    private val address by lazy { requireArguments().getString("address") ?: ""}
    private val vm: CartViewModel by activityViewModels()
    private val voucherUsed: VoucherUsedViewModel by activityViewModels()
    private val formatter = DecimalFormat("0.00")
    private val nav by lazy { findNavController() }
    private val model: LoginViewModel by activityViewModels()
    private val order: OrderViewModel by activityViewModels()
    private val orderFood: OrderFoodViewModel by activityViewModels()
    private val seller: SellerViewModel by activityViewModels()
    private val cardvm: CardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPaymentMethodBinding.inflate(inflater,container,false)

        binding.spnSelect.isEnabled = false

        var card = ArrayList<String>()

        lifecycleScope.launch{

            Firebase.firestore.collection("Card").whereEqualTo("userId",model.user.value!!.id).get().addOnSuccessListener {
                    snap ->
                val list = snap.toObjects<Card>()

                    list.forEach { l ->

                        card.add(l.name + " \n" + l.cardNo)
                    }
                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    card
                )
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spnSelect.adapter = arrayAdapter
            }


        }


      binding.radCash.setOnClickListener{
          binding.spnSelect.isEnabled = false
      }

        binding.radCard.setOnClickListener{
            binding.spnSelect.isEnabled = true
            lifecycleScope.launch{

                Firebase.firestore.collection("Card").whereEqualTo("userId",model.user.value!!.id).get().addOnSuccessListener {
                        snap ->
                    val list = snap.toObjects<Card>()
                    if(list.isEmpty()){
                        AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_error)
                            .setTitle("Error")
                            .setMessage("Card is empty")
                            .setPositiveButton("Back", null)
                            .show()
                        nav.navigate(R.id.insertCardFragment)
                    }
                }

            }

        }

        binding.btnSubmit.setOnClickListener {
            if(binding.radCash.isChecked){
                nav.navigate(
                    R.id.paymentFragment,
                    bundleOf("id" to total,
                        "shop" to shop,
                        "voucher" to voucher,
                        "voucher name" to voucherName,
                        "voucherId" to voucherId,
                        "code" to code,
                        "address" to address,
                        "method" to "Pay By Cash",
                    )
                )
            }else{
                nav.navigate(
                    R.id.paymentFragment,
                    bundleOf("id" to total,
                        "shop" to shop,
                        "voucher" to voucher,
                        "voucher name" to voucherName,
                        "voucherId" to voucherId,
                        "code" to code,
                        "address" to address,
                        "method" to "Pay By Card - ${binding.spnSelect.selectedItem}",
                    )
                )
            }
        }

        return binding.root
    }


}