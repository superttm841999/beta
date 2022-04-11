package com.example.beta.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beta.R
import com.example.beta.data.Cart

class CartAdapter (
    // Callback function
    val fn: (ViewHolder, Cart) -> Unit = { _, _ -> }
) : ListAdapter<Cart, CartAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(a: Cart, b: Cart)    = a.id == b.id
        override fun areContentsTheSame(a: Cart, b: Cart) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtId    : TextView = view.findViewById(R.id.txtId)
        val txtName  : TextView = view.findViewById(R.id.txtName)
        val txtPrice : TextView = view.findViewById(R.id.txtPrice)
        val txtCount : TextView = view.findViewById(R.id.txtCount)
        val btnEdit  : ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.txtId.text    = item.id
        holder.txtName.text  = item.name
        holder.txtCount.text  = item.count.toString()
        holder.txtPrice.text = "%.2f".format(item.price)
        fn(holder, item)
    }
}