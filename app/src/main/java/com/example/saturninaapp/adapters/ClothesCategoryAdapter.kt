package com.example.saturninaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.viewholder.ClothesCategoryViewHolder

class ClothesCategoryAdapter(private val clothesCategories: List<ClothCategoryData>, private val onCategorySelected : (Int) -> Unit)
    :RecyclerView.Adapter<ClothesCategoryViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categories, parent, false)
        return ClothesCategoryViewHolder(view)
    }


    override fun onBindViewHolder(holder: ClothesCategoryViewHolder, position: Int) {
        holder.render(clothesCategories[position],onCategorySelected)
    }

    override fun getItemCount(): Int = clothesCategories.size

}