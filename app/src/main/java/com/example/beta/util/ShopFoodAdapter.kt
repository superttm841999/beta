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
import java.text.DecimalFormat

class ShopFoodAdapter (
    val fn: (ViewHolder, Food) -> Unit = { _, _ -> }
) : ListAdapter<Food, ShopFoodAdapter.ViewHolder>(DiffCallback) {

    private val formatter = DecimalFormat("0.00")

    companion object DiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(a: Food, b: Food)    = a.id == b.id
        override fun areContentsTheSame(a: Food, b: Food) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtId      : TextView = view.findViewById(R.id.txtFoodId)
        val txtName    : TextView = view.findViewById(R.id.txtFoodName)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
        val txtPrice   : TextView = view.findViewById(R.id.txtFoodPrice)
        val imgFood : ImageView = view.findViewById(R.id.imgFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_food_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = getItem(position)

        holder.txtId.text    = food.id
        holder.txtName.text  = food.name
        holder.txtPrice.text = formatter.format(food.price)

        // TODO(13): Display [category.name]
        holder.txtStatus.text = food.status
        holder.imgFood.setImageBitmap(food.image.toBitmap())

        fn(holder, food)
    }

}