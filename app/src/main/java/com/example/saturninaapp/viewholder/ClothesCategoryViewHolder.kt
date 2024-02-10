package com.example.saturninaapp.viewholder

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.ClothCategoryData

class ClothesCategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
    private val containerCategoryCard: CardView = view.findViewById(R.id.containerCategoryCard)

    fun render(clothCategoryData: ClothCategoryData, onCategorySelected: (Int) -> Unit) {

        val color = if(clothCategoryData.isSelectedCategory){
            R.color.black
        }else{
            R.color.g_gray700
        }
        containerCategoryCard.setCardBackgroundColor(ContextCompat.getColor(containerCategoryCard.context, color))
        tvCategoryName.setBackgroundColor(ContextCompat.getColor(containerCategoryCard.context, color))

        itemView.setOnClickListener { onCategorySelected(layoutPosition) }

        tvCategoryName.text = clothCategoryData.name
    }

}