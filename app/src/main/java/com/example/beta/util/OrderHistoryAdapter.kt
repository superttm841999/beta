package com.example.beta.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beta.R
import com.example.beta.data.Order
import com.example.beta.data.Seller
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class OrderHistoryAdapter (
    val fn: (ViewHolder, Order) -> Unit = { _, _ -> }
) : ListAdapter<Order, OrderHistoryAdapter.ViewHolder>(DiffCallback) {

    private val formatter = DecimalFormat("0.00")

    companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(a: Order, b: Order)    = a.docId == b.docId
        override fun areContentsTheSame(a: Order, b: Order) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtOrderId : TextView = (view.findViewById(R.id.txtOrderId))
        val txtStatus : TextView = (view.findViewById(R.id.txtStatus))
        val imgLogo : ImageView = view.findViewById(R.id.imgItemShop)
        val txtName : TextView = view.findViewById(R.id.txtItemShopName)
        val txtTotal: TextView = view.findViewById(R.id.txtPrice)
        val txtCount: TextView = view.findViewById(R.id.txtCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_order_history_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = getItem(position)


            Firebase.firestore.collection("Seller").get().addOnSuccessListener {
                    snap ->
                val list = snap.toObjects<Seller>()
                list.forEach { l ->
                    if(l.docId == order.sellerId){
                        holder.imgLogo.setImageBitmap(l.logo.toBitmap())
                        holder.txtName.text  = l.name
                        when(order.status){
                            0 -> holder.txtStatus.text  = "Pending"
                            1 -> holder.txtStatus.text  = "Accepted"
                            2 -> holder.txtStatus.text = "Rejected"
                            else -> holder.txtStatus.text = "What?!"
                        }
                        holder.txtOrderId.text  = "Order Id # ${order.docId}"
                        holder.txtTotal.text  = "RM ${formatter.format(order.payment)}"
                        holder.txtCount.text = "- ${order.count} food(s)"
                        fn(holder, order)
                    }

                }

            }


    }

}