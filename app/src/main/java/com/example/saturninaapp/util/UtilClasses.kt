package com.example.saturninaapp.util

import android.view.MenuItem
import android.view.View
import com.example.saturninaapp.models.DetailProduct
import com.google.android.material.bottomnavigation.BottomNavigationItemView

interface UtilClasses {

    abstract fun onItemClothSelected(product: DetailProduct)


    abstract fun onItemDeleteSelected(product: DetailProduct)


    abstract fun onSizeSelected(product: DetailProduct, size: String)
}