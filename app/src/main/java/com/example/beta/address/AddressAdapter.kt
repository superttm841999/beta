package com.example.beta.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beta.R

class AddressAdapter(
    val fn: (ViewHolder, Address) -> Unit = { _, _ -> }
) : ListAdapter<Address, AddressAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(a: Address, b: Address)    = a.id == b.id
        override fun areContentsTheSame(a: Address, b: Address) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val name            : TextView = view.findViewById(R.id.name)
        val phone           : TextView = view.findViewById(R.id.phone)
        val detailAddress   : TextView = view.findViewById(R.id.detailAddress)
        val state           : TextView = view.findViewById(R.id.state)
        val district        : TextView = view.findViewById(R.id.district)
        val postalCode      : TextView = view.findViewById(R.id.postalCode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.address_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = getItem(position)

        val default = when(address.default){
            0 -> ""
            1 -> "[Default] "
            else -> ""
        }
        holder.name.text   = default + address.name
        holder.phone.text = address.phone
        holder.detailAddress.text  = address.detailAddress
        holder.state.text = address.state
        holder.district.text = address.district
        holder.postalCode.text = address.postalCode

        fn(holder, address)
    }
}