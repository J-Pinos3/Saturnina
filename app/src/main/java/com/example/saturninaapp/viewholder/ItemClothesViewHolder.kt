package com.example.saturninaapp.viewholder

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.Colore
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.Talla
import com.squareup.picasso.Picasso

class ItemClothesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val tvClothItemDetail: TextView = view.findViewById(R.id.tvClothItemDetail)
    private val tvClothItemPrice: TextView = view.findViewById(R.id.tvClothItemPrice)
    private val tvClothItemDescription: TextView = view.findViewById(R.id.tvClothItemDescription)
    private val ivClothItemPhoto: ImageView = view.findViewById(R.id.ivClothItemPhoto)
    private val spSizesChoice: Spinner = view.findViewById(R.id.spSizesChoice)
    private val spColorsChoice: Spinner = view.findViewById(R.id.spColorsChoice)

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
        onHideItemCounter: (view: View, isVisible: Boolean) -> Unit,
        onChooseSize: (DetailProduct, size: String) -> Unit
    ){

        OnHideButton(btnDeleteFromCart, isVisible)
        onHideItemCounter(tvItemCounter as View, isVisible)

        loadSizesSpinner(spSizesChoice, detailProduct)
        loadColorsSpinner(spColorsChoice, detailProduct)
        tvClothItemDetail.text = detailProduct.name
        tvClothItemPrice.text = "$" + detailProduct.precio
        tvClothItemDescription.text = detailProduct.descripcion
        tvItemCounter.text = detailProduct.contador.toString()

        //imagen[0]  because now its a List of images for the moment I'll just show the first one till I implement the carousel of images
        ivClothItemPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        Picasso.get().
            load(detailProduct.imagen[0].secure_url)
            .fit()
            .centerCrop()
            .into(ivClothItemPhoto)


        spSizesChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val items = getNameofSizes(detailProduct.tallas)
                val tallaString = items.get(p2)
                onChooseSize(detailProduct, tallaString)
                Log.i("SELECTED SIZE", "you choose $tallaString ")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //
            }

        }



        btnAddToCart.setOnClickListener {
            OnCLickListener(detailProduct)
        }

        btnDeleteFromCart.setOnClickListener {
            OnItemDeleteListener(detailProduct)
        }

    }

    private fun loadSizesSpinner(spinner: Spinner, detProd: DetailProduct){
        val sizesList = getNameofSizes(detProd.tallas)
        val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, sizesList )
        spinner.adapter = adapter
    }

    private fun getNameofSizes(listSizes: List<Talla>): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        for(k in listSizes){
            listaNombres.add(k.name)
        }
        return listaNombres
    }



    private fun loadColorsSpinner(spinner: Spinner, detProd: DetailProduct){
        val colorsList = getNameofColores(detProd.colores)
        val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, colorsList )
        spinner.adapter = adapter
    }


    private fun getNameofColores(listColors: List<Colore>): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        for(k in listColors){
            listaNombres.add(k.name)
        }
        return listaNombres
    }



}