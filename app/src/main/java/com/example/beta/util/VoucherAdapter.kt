package com.example.beta.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beta.R
import com.example.beta.data.Voucher
import java.text.SimpleDateFormat
import java.util.*

class VoucherAdapter (
    val fn: (ViewHolder, Voucher) -> Unit = { _, _ -> }
) : ListAdapter<Voucher, VoucherAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(a: Voucher, b: Voucher)    = a.docId == b.docId
        override fun areContentsTheSame(a: Voucher, b: Voucher) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtName  : TextView = view.findViewById(R.id.txtName)
        val txtCode  : TextView = view.findViewById(R.id.txtCode)
        val txtDate   : TextView = view.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_voucher_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voucher = getItem(position)
        val date = Date()
        val format = SimpleDateFormat("yyyy/MM/dd")

        val cmp =
            voucher?.startDate?.let { it1 ->
                format.format(date).toString().compareTo(
                    it1
                )
            } //-1
        val cmpEnd =
            voucher?.endDate?.let { it1 ->
                format.format(date).toString().compareTo(
                    it1
                )
            } //-1

        if(voucher.status == 1){
            if (cmp != null && cmpEnd !=null) {
                if(cmp >= 0 && cmpEnd <= 0) {
                    holder.txtName.text = voucher.name
                    holder.txtDate.text = "Available: ${voucher.startDate} - ${voucher.endDate}"
                }else{
                    holder.txtName.text = voucher.name
                    holder.txtDate.text = "Coming soon or expired"
                }
            }
        }


        fn(holder, voucher)
    }

}