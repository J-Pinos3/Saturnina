package com.example.saturninaapp.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.DetailProduct
import com.squareup.picasso.Picasso

class ItemClothesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val tvClothItemDetail: TextView = view.findViewById(R.id.tvClothItemDetail)
    private val tvClothItemPrice: TextView = view.findViewById(R.id.tvClothItemPrice)
    private val tvClothItemDescription: TextView = view.findViewById(R.id.tvClothItemDescription)
    private val ivClothItemPhoto: ImageView = view.findViewById(R.id.ivClothItemPhoto)

    private val btnAddToCart: View = view.findViewById(R.id.btnAddToCart)
    private val btnDeleteFromCart: View = view.findViewById(R.id.btnDeleteFromCart)

    private val tvItemCounter: TextView = view.findViewById(R.id.tvItemCounter)

    //where itemClothes is a data class
    fun render(
        detailProduct: DetailProduct,
        OnCLickListener: (DetailProduct) -> Unit,
        OnItemDeleteListener: (DetailProduct) -> Unit,
        OnHideButton: (view: View, isVisible: Boolean) -> Unit,
        isVisible: Boolean,
        onHideItemCounter: (view: View, isVisible: Boolean) -> Unit
    ){

        OnHideButton(btnDeleteFromCart, isVisible)
        onHideItemCounter(tvItemCounter as View, isVisible)

        tvClothItemDetail.text = detailProduct.name
        tvClothItemPrice.text = "$" + detailProduct.precio
        tvClothItemDescription.text = detailProduct.descripcion
        tvItemCounter.text = detailProduct.contador.toString()
        Picasso.get().load(detailProduct.imagen.secure_url).into(ivClothItemPhoto)


        btnAddToCart.setOnClickListener {
            OnCLickListener(detailProduct)
        }

        btnDeleteFromCart.setOnClickListener {
            OnItemDeleteListener(detailProduct)
        }

    }
}