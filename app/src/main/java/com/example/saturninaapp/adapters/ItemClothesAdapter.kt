package com.example.saturninaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.viewholder.ItemClothesViewHolder


class ItemClothesAdapter(
    var sellingItems: MutableList<DetailProduct>,
    val OnCLickListener: (DetailProduct) -> Unit,
    val OnItemDeleteListener: (DetailProduct) -> Unit,
    val OnHideButton: (view: View, isVisible: Boolean) -> Unit,
    var isVisible: Boolean,
    val onHideItemCounter: (view: View, isVisible: Boolean) -> Unit,
    val onChooseSize: (spinner: AutoCompleteTextView, DetailProduct) -> Boolean,
    val onChooseColor: (spinner: AutoCompleteTextView, DetailProduct) -> Boolean,
    val UserROL: String
)
    :RecyclerView.Adapter<ItemClothesViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemClothesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clothes, parent, false)
        return ItemClothesViewHolder(view)
    }


    override fun onBindViewHolder(holder: ItemClothesViewHolder, position: Int) {
        holder.render(sellingItems[position], OnCLickListener, OnItemDeleteListener, OnHideButton, isVisible, onHideItemCounter, onChooseSize, onChooseColor,UserROL)
    }

    override fun getItemCount(): Int = sellingItems.size
}