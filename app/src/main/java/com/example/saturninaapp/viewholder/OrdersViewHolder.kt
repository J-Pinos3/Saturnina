package com.example.saturninaapp.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.OrderResult

class OrdersViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val ivIconShowSale: ImageView = view.findViewById(R.id.ivIconShowSale)
    private val tvSalesUserName: TextView = view.findViewById(R.id.tvSalesUserName)
    private val tvSalesProductName: TextView = view.findViewById(R.id.tvSalesProductName)
    private val tvSalesProductPrice: TextView = view.findViewById(R.id.tvSalesProductPrice)
    private val tvSalesDate: TextView = view.findViewById(R.id.tvSalesDate)
    private val tvSalesStatus: TextView = view.findViewById(R.id.tvSalesStatus)

    private val vColorState: View = view.findViewById(R.id.vColorState)
    private val statusList: Array<String> = arrayOf("Pendiente","Rechazado","En entrega","Finalizado")

    @SuppressLint("SetTextI18n")
    fun render(orderResult: OrderResult,
               OnShowOrderInfo: (OrderResult) -> Unit){

        ivIconShowSale.setOnClickListener {
            OnShowOrderInfo(orderResult)
        }

        tvSalesUserName.text = orderResult.id_orden.nombre + " " + orderResult.id_orden.apellido
        tvSalesProductName.text = orderResult.id_producto.name
        tvSalesProductPrice.text = orderResult.id_producto.precio.toString()
        tvSalesDate.text = orderResult.fecha.substring(0,19)
        tvSalesStatus.text = orderResult.status

        val color = when(orderResult.status){
            statusList[0] ->  Color.argb(255,236,172,53)
            statusList[1] ->  Color.RED
            statusList[2] ->  Color.argb(255,236,172,53)
            statusList[3] ->  Color.GREEN
                    else  ->  Color.GRAY
        }

        //vColorState.setBackgroundColor(color)
        vColorState.background.setTint(color)

    }
}