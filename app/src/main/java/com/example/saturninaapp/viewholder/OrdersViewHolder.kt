package com.example.saturninaapp.viewholder

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.OrderResult

class OrdersViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val ivIconShowSale: ImageView = view.findViewById(R.id.ivIconShowSale)
    private val tvSalesUserName: TextView = view.findViewById(R.id.tvSalesUserName)
    private val tvSalesProductName: TextView = view.findViewById(R.id.tvSalesProductName)
    private val tvSalesProductPrice: TextView = view.findViewById(R.id.tvSalesProductPrice)


    @SuppressLint("SetTextI18n")
    fun render(orderResult: OrderResult,
               OnShowOrderInfo: (OrderResult) -> Unit){

        ivIconShowSale.setOnClickListener {
            OnShowOrderInfo(orderResult)
        }

        tvSalesUserName.text = orderResult.id_orden.nombre + " " + orderResult.id_orden.apellido
        tvSalesProductName.text = orderResult.id_producto.name
        tvSalesProductPrice.text = orderResult.id_producto.precio.toString()
    }
}