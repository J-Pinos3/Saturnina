package com.example.saturninaapp.viewholder

import android.annotation.SuppressLint
import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.DetailProduct
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class ClothesCarouselViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val ivClothIntroPhoto: ImageView = view.findViewById(R.id.ivClothIntroPhoto)
    private val tvClothIntroDetail: TextView = view.findViewById(R.id.tvClothIntroDetail)//product name
    private val tvClothIntroDescription: TextView = view.findViewById(R.id.tvClothIntroDescription)
    private val tvClothIntroPrice: TextView = view.findViewById(R.id.tvClothIntroPrice)


    fun render(Product: DetailProduct){
        tvClothIntroDetail.text = Product.name
        tvClothIntroDescription.text = Product.descripcion
        tvClothIntroPrice.text = "$" + Product.precio

        ivClothIntroPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        Picasso.get().
        load(Product.imagen.secure_url)
        .fit()
        .centerCrop()
        .into(ivClothIntroPhoto)
    }
}