package com.example.saturninaapp.viewholder

import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
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
    val spSizesChoice: AutoCompleteTextView = view.findViewById(R.id.spSizesChoice)
    private val spColorsChoice: AutoCompleteTextView = view.findViewById(R.id.spColorsChoice)

    private val btnAddToCart: View = view.findViewById(R.id.btnAddToCart)
    private val btnDeleteFromCart: View = view.findViewById(R.id.btnDeleteFromCart)

    private val tvItemCounter: TextView = view.findViewById(R.id.tvItemCounter)
    private lateinit var sizesList: List<String>

    //where itemClothes is a data class
    fun render(
        detailProduct: DetailProduct,
        OnCLickListener: (DetailProduct) -> Unit,
        OnItemDeleteListener: (DetailProduct) -> Unit,
        OnHideButton: (view: View, isVisible: Boolean) -> Unit,
        isVisible: Boolean,
        onHideItemCounter: (view: View, isVisible: Boolean) -> Unit,
        onChooseSize: (spinner: AutoCompleteTextView, DetailProduct) -> Unit,
        onChooseColor: (spinner: AutoCompleteTextView, DetailProduct) -> Unit,
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


        onChooseSize(spSizesChoice, detailProduct)
        onChooseColor(spColorsChoice, detailProduct)

        spSizesChoice.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                detailProduct.tallaSeleccionada = parent?.getItemAtPosition(position).toString()
                spSizesChoice.post{
                    spSizesChoice.text = Editable.Factory.getInstance().newEditable(detailProduct.tallaSeleccionada)
                }
                Log.i("SELECTED SIZE", "you choose ${detailProduct}")
            }


        spColorsChoice.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                detailProduct.colorSeleccionado = parent?.getItemAtPosition(position).toString()
                spColorsChoice.post{
                    spColorsChoice.text = Editable.Factory.getInstance().newEditable(detailProduct.colorSeleccionado)
                }
                Log.i("SELECTED COLOR", "you choose ${detailProduct}")
        }


        btnAddToCart.setOnClickListener {
            OnCLickListener(detailProduct)
        }

        btnDeleteFromCart.setOnClickListener {
            OnItemDeleteListener(detailProduct)
        }

    }
    private fun loadSizesSpinner(spinner: AutoCompleteTextView, detProd: DetailProduct){
        sizesList = getNameofSizes(detProd.tallas)
        val adapter = ArrayAdapter(itemView.context, R.layout.list_size, sizesList )
        spinner.setAdapter(adapter)
    }

    private fun getNameofSizes(listSizes: List<Talla>): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        for(k in listSizes){
            listaNombres.add(k.name)
        }
        return listaNombres
    }

//    private fun setInitialSizeNavigate(spinner: AutoCompleteTextView, detProd: DetailProduct){
//        if(  !detProd.tallaSeleccionada.isNullOrEmpty() ){
//            var indiceTalla = sizesList.indexOf(detProd.tallaSeleccionada)
//            spinner.setSelection(indiceTalla)
//        }else{
//            var element = sizesList.elementAt(0)
//            spinner.setSelection( sizesList.indexOf(element) )
//        }
//    }





    private fun loadColorsSpinner(spinner: AutoCompleteTextView, detProd: DetailProduct){
        val colorsList = getNameofColores(detProd.colores)
        val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, colorsList )
        spinner.setAdapter(adapter)
    }


    private fun getNameofColores(listColors: List<Colore>): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        for(k in listColors){
            listaNombres.add(k.name)
        }
        return listaNombres
    }


}