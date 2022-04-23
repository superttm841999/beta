package com.example.beta.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beta.R
import com.example.beta.address.AddressViewModel
import com.example.beta.data.CartViewModel
import com.example.beta.data.VoucherUsed
import com.example.beta.data.VoucherUsedViewModel
import com.example.beta.data.VoucherViewModel
import com.example.beta.databinding.FragmentCartListBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.CartAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CartListFragment : Fragment() {

    private lateinit var binding:FragmentCartListBinding
    private val nav by lazy { findNavController() }
    private val vm: CartViewModel by activityViewModels()
    private val vc: VoucherViewModel by activityViewModels()
    private val am: AddressViewModel by activityViewModels()
    private val voucherUsed: VoucherUsedViewModel by activityViewModels()
    private val formatter = DecimalFormat("0.00")
    private val shop_name by lazy { requireArguments().getString("shop_name") ?: ""}
    private val date = Date()
    private val model: LoginViewModel by activityViewModels()

    private val format = SimpleDateFormat("yyyy/MM/dd")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCartListBinding.inflate(inflater, container, false)

        val vvv =  runBlocking {vc.get(binding.edtVoucher.text.toString())}
        Log.d("try", runBlocking {vc.get(binding.edtVoucher.text.toString())}.toString())

        val getId = vvv?.docId
        if(binding.edtVoucher.text.toString().isNotEmpty()){
            voucherUsed.voucherList.observe(viewLifecycleOwner){ list ->
                list.forEach { f->
                    if(f.voucherId == getId && f.username == model.user.value!!.username){
                        AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_error)
                            .setTitle("Error")
                            .setMessage("You have used this code already")
                            .setPositiveButton("Dismiss", null)
                            .show()
                        nav.navigateUp()
                    }
                }
            }
        }



        var address = ArrayList<String>()

        am.addresses.observe(viewLifecycleOwner){_ ->
            if (am.getSize() != 0) {
                for (a in am.getDefaultAtFirst()!!) {
                    address.add(a.detailAddress)
                }
            }else{
                address.add("--SELECT--")
            }


                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    address
                )
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spnAddress.adapter = arrayAdapter
                //Log.d("checkb",shop_name.distinct().toString())

        }

            voucherUsed.checkVoucherUsed("",model.user.value!!.username)
            voucherUsed.voucherList1.observe(viewLifecycleOwner){

            }




        val adapter = CartAdapter() { holder, f ->
            // holder.root.setOnClickListener      { nav.navigate(R.id.detailFragment, bundleOf("id" to f.id)) }
            holder.btnEdit.setOnClickListener   { nav.navigate(R.id.foodDetailFragment, bundleOf("id" to f.id,"shop" to f.shop_name) ) }
            holder.btnDelete.setOnClickListener {
                vm.delete(f)
                nav.navigate(R.id.shopCartFragment)
            }
        }

        binding.rv.adapter = adapter

            vm.getShop(shop_name,model.user.value!!.username).observe(viewLifecycleOwner){carts ->
                adapter.submitList(carts)
            }


        binding.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.btnBack.setOnClickListener {
            nav.navigate(R.id.shopFoodFragment)
        }
        binding.btnDeleteAll.setOnClickListener { vm.deleteAll(model.user.value!!.username)
        vm.deleteShopAll(model.user.value!!.username)
        }


        var total = 0.00

        binding.btnPay.setOnClickListener {
            if(binding.spnAddress.selectedItem.toString()=="--SELECT--"){
                AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_error)
                    .setTitle("Error")
                    .setMessage("Address is Not Found")
                    .setPositiveButton("Dismiss", null)
                    .show()
                nav.navigate(R.id.addressAddFragment3)
            }else{
                vm.getShop(shop_name,model.user.value!!.username).observe(viewLifecycleOwner){carts ->
                    val vvv =  runBlocking {vc.get(binding.edtVoucher.text.toString())}
                    Log.d("try", runBlocking {vc.get(binding.edtVoucher.text.toString())}.toString())

                    val getId = vvv?.docId
                    Log.d("try1",  getId.toString())
                    val err = vvv?.let { it1 -> vc.validate(it1) }
                    Log.d("test",err.toString())

                        if(binding.edtVoucher.text.toString().isEmpty()){

                            for(c in carts){
                                total += (c.price * c.count)
                            }
                            nav.navigate(R.id.paymentMethodFragment,
                                bundleOf("id" to total,
                                    "shop" to shop_name,
                                    "voucher" to 0,
                                    "voucher name" to "No Voucher",
                                    "voucherId" to "No Id",
                                    "code" to "No Code",
                                    "address" to binding.spnAddress.selectedItem.toString()
                                )
                            )
                        }else{

                            val cmp =
                                vvv?.startDate?.let { it1 ->
                                    format.format(date).toString().compareTo(
                                        it1
                                    )
                                } //-1
                            val cmpEnd =
                                vvv?.endDate?.let { it1 ->
                                    format.format(date).toString().compareTo(
                                        it1
                                    )
                                } //-1

                            Log.d("check",cmp.toString())
                            Log.d("checks",cmpEnd.toString())
                            if(err == true && vvv.status == 1 ){
                                if (cmp != null && cmpEnd !=null) {
                                    if(cmp >= 0 && cmpEnd <= 0){
                                        voucherUsed.checkVoucherUsed(getId!!,model.user.value!!.username)
                                        voucherUsed.voucherList1.observe(viewLifecycleOwner){ list ->
                                                if(list.isNotEmpty()){
                                                    AlertDialog.Builder(context)
                                                        .setIcon(R.drawable.ic_error)
                                                        .setTitle("Error")
                                                        .setMessage("You have used this code already")
                                                        .setPositiveButton("Back", null)
                                                        .show()
                                                    return@observe
                                                }else{
                                                    for(c in carts){
                                                        total += (c.price * c.count)
                                                    }
                                                    nav.navigate(R.id.paymentMethodFragment,
                                                        bundleOf("id" to total,
                                                            "shop" to shop_name,
                                                            "voucher" to vvv?.value,
                                                            "voucher name" to vvv?.name,
                                                            "voucherId" to vvv?.docId,
                                                            "code" to vvv?.code,
                                                            "address" to binding.spnAddress.selectedItem.toString()
                                                        )
                                                    )
                                                }

                                        }
                                    }else{
                                        AlertDialog.Builder(context)
                                            .setIcon(R.drawable.ic_error)
                                            .setTitle("Error")
                                            .setMessage("Code is expired")
                                            .setPositiveButton("Dismiss", null)
                                            .show()
                                        return@observe
                                    }

                                }
                            }else{
                                AlertDialog.Builder(context)
                                    .setIcon(R.drawable.ic_error)
                                    .setTitle("Error")
                                    .setMessage("Code is no found")
                                    .setPositiveButton("Dismiss", null)
                                    .show()
                                return@observe
                            }
                        }


//                binding.txtCount.text = formatter.format(total)
                    Log.d("lol",total.toString())
                }
            }

        }

        return binding.root
    }

}