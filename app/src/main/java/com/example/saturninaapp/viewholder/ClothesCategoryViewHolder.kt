package com.example.saturninaapp.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.ClothCategoryData

class ClothesCategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)

    fun render(clothCategoryData: ClothCategoryData){
        tvCategoryName.text = clothCategoryData.name
    }
}