package com.example.saturninaapp.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.DetailProduct
import com.squareup.picasso.Picasso

class ClothesCarouselViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val ivClothIntroPhoto: ImageView = view.findViewById(R.id.ivClothIntroPhoto)
    private val tvClothIntroDetail: TextView = view.findViewById(R.id.tvClothIntroDetail)//product name
    private val tvClothIntroDescription: TextView = view.findViewById(R.id.tvClothIntroDescription)
    private val tvClothIntroPrice: TextView = view.findViewById(R.id.tvClothIntroPrice)


    fun render(Product: DetailProduct){
        tvClothIntroDetail.text = Product.name
        tvClothIntroDescription.text = Product.descripcion
        tvClothIntroPrice.text = "$" + Product.precio

        //imagen[0]  because now its a List of images for the moment I'll just show the first one till I implement the carousel of images
        ivClothIntroPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        Picasso.get().
        load(Product.imagen[0].secure_url)
        .fit()
        .centerCrop()
        .into(ivClothIntroPhoto)
    }
}

//        val imageUrl = when (val imagen = Product.imagen) {
//            is List<*> -> {
//                if (imagen.isNotEmpty()) {
//                    (imagen[0] as Imagen).secure_url
//                } else {
//                    null
//                }
//            }
//            is Imagen -> {
//                imagen.secure_url
//            }
//            else -> null
//        }