package com.example.beta.util

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beta.R
import com.example.beta.data.Cart
import com.example.beta.data.Seller
import com.example.beta.data.Shop
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class ShopCartAdapter (
    // Callback function
    val fn: (ViewHolder, Shop) -> Unit = { _, _ -> }
) : ListAdapter<Shop, ShopCartAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Shop>() {
        override fun areItemsTheSame(a: Shop, b: Shop)    = a.id == b.id
        override fun areContentsTheSame(a: Shop, b: Shop) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtName  : TextView = view.findViewById(R.id.txtName)
        val imgShop : ImageView = view.findViewById(R.id.imgShop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_shop_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        Firebase.firestore.collection("Seller").get().addOnSuccessListener {
                snap ->
            val list = snap.toObjects<Seller>()
                   list.forEach{ l ->
                       if(l.name == item.name){
                           holder.txtName.text = l.name
                           holder.imgShop.setImageBitmap(l.logo.toBitmap())
                       }
                   }
        }
        fn(holder, item)
    }
}