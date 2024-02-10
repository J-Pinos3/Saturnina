package com.example.saturninaapp.util

import android.widget.AutoCompleteTextView
import com.example.saturninaapp.models.DetailProduct

interface UtilClasses {

    abstract fun onItemClothSelected(product: DetailProduct)


    abstract fun onItemDeleteSelected(product: DetailProduct)


    abstract fun onSizeSelected(spinner: AutoCompleteTextView, product: DetailProduct): Boolean

    abstract fun onColorSelected(spinner: AutoCompleteTextView, product: DetailProduct): Boolean
}