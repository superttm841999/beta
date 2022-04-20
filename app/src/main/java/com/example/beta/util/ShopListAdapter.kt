package com.example.beta.util

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beta.R
import com.example.beta.data.Seller
import java.text.SimpleDateFormat
import java.util.*

class ShopListAdapter (
    val fn: (ViewHolder, Seller) -> Unit = { _, _ -> }
) : ListAdapter<Seller, ShopListAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Seller>() {
        override fun areItemsTheSame(a: Seller, b: Seller)    = a.docId == b.docId
        override fun areContentsTheSame(a: Seller, b: Seller) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val imgLogo : ImageView = view.findViewById(R.id.imgItemShop)
        val txtName : TextView = view.findViewById(R.id.txtItemShopName)
        val txtCount: TextView = view.findViewById(R.id.txtItemShopCount)
        val txtShopStatus: TextView = view.findViewById(R.id.txtShopStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_seller_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = getItem(position)
        val format = SimpleDateFormat("hhmm")
        val date = Date()

        Log.d("date",format.format(date).toString())
        if(application.status == 1){

            holder.imgLogo.setImageBitmap(application.logo.toBitmap())
            holder.txtName.text  = application.name

            val cmp =
                application?.open?.let { it1 ->
                    format.format(date).toString().compareTo(
                        it1
                    )
                } //-1

            val cmpEnd =
                application?.close?.let { it1 ->
                    format.format(date).toString().compareTo(
                        it1
                    )
                } //-1
            Log.d("date1",cmp.toString())
            Log.d("date2",cmpEnd.toString())
            if (cmp != null && cmpEnd !=null) {
                if(cmp >= 0 && cmpEnd <= 0){
                    holder.txtShopStatus.text = "OPEN"
                    holder.txtShopStatus.setTextColor(Color.parseColor("#008000"))
                }
                else{
                    holder.txtShopStatus.text = "CLOSED"
                    holder.txtShopStatus.setTextColor(Color.parseColor("#FF0000"))
                }
            }
        }

            holder.txtCount.text = "${application.count} Food(s) "
            fn(holder, application)
        }
}

