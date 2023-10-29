package com.example.saturninaapp.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.ItemClothes

class ItemClothesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val tvClothItemDetail: TextView = view.findViewById(R.id.tvClothItemDetail)
    private val tvClothItemPrice: TextView = view.findViewById(R.id.tvClothItemPrice)

    //where itemClothes is a data class
    fun render(itemClothes: ItemClothes){
        tvClothItemDetail.text = itemClothes.detailName
        tvClothItemPrice.text = "$" + itemClothes.price
    }
}