package com.example.saturninaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.ItemClothes
import com.example.saturninaapp.viewholder.ItemClothesViewHolder

class ItemClothesAdapter(private val sellingItems: List<ItemClothes>)
    :RecyclerView.Adapter<ItemClothesViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemClothesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clothes, parent, false)
        return ItemClothesViewHolder(view)
    }


    override fun onBindViewHolder(holder: ItemClothesViewHolder, position: Int) {
        holder.render(sellingItems[position])
    }

    override fun getItemCount(): Int = sellingItems.size

}