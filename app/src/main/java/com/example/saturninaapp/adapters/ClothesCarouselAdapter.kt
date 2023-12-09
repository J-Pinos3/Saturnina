package com.example.saturninaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.viewholder.ClothesCarouselViewHolder


class ClothesCarouselAdapter(var productSections: MutableList< DetailProduct> )
    :RecyclerView.Adapter<ClothesCarouselViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesCarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.intro_carousel_item, parent, false)
        return ClothesCarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClothesCarouselViewHolder, position: Int) {
        holder.render(productSections[position])

    }

    //just show 5 products, I'll show the complete list of products by category in anotheR activity
    //override fun getItemCount(): Int =  minOf(productSections.size, 5)
    override fun getItemCount(): Int =  productSections.size

}