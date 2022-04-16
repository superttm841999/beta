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
import com.example.beta.data.Food
import com.example.beta.data.OrderFood
import com.example.beta.data.Seller
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class OrderHistoryDetailAdapter (
    val fn: (ViewHolder, OrderFood) -> Unit = { _, _ -> }
) : ListAdapter<OrderFood, OrderHistoryDetailAdapter.ViewHolder>(DiffCallback) {

    private val formatter = DecimalFormat("0.00")

    companion object DiffCallback : DiffUtil.ItemCallback<OrderFood>() {
        override fun areItemsTheSame(a: OrderFood, b: OrderFood)    = a.docId == b.docId
        override fun areContentsTheSame(a: OrderFood, b: OrderFood) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtId      : TextView = view.findViewById(R.id.txtFoodId)
        val txtName    : TextView = view.findViewById(R.id.txtFoodName)
        val txtQty: TextView = view.findViewById(R.id.txtQty)
        val txtPrice   : TextView = view.findViewById(R.id.txtFoodPrice)
        val imgFood : ImageView = view.findViewById(R.id.imgFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_order_history_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = getItem(position)



        Firebase.firestore.collection("Food").get().addOnSuccessListener {
                snap ->
            val list = snap.toObjects<Food>()
            list.forEach { l ->
                if(l.id == order.foodId){
                    holder.txtId.text  = order.orderId
                    holder.imgFood.setImageBitmap(l.image.toBitmap())
                    holder.txtName.text  = l.name
                    holder.txtPrice.text  = "RM ${formatter.format(l.price)}"
                    holder.txtQty.text = "X ${order.quantity}"
                    fn(holder, order)
                }

            }

        }
    }

}