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
import com.example.beta.data.Card
import java.text.SimpleDateFormat
import java.util.*

class CardAdapter (
    val fn: (ViewHolder, Card) -> Unit = { _, _ -> }
) : ListAdapter<Card, CardAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(a: Card, b: Card)    = a.docId == b.docId
        override fun areContentsTheSame(a: Card, b: Card) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtName  : TextView = view.findViewById(R.id.txtName)
        val txtCardNo   : TextView = view.findViewById(R.id.txtCardNo)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_card_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = getItem(position)

        holder.txtName.text   = card.name
        holder.txtCardNo.text  = card.cardNo

        fn(holder, card)
    }

}