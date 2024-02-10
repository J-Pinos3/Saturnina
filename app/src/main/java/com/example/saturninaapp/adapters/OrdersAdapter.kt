package com.example.saturninaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.OrderResult
import com.example.saturninaapp.viewholder.OrdersViewHolder

class OrdersAdapter(private var ordersList: MutableList<OrderResult>,
    val OnShowOrderInfo: (OrderResult)-> Unit )
    : RecyclerView.Adapter<OrdersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrdersViewHolder(view)
    }


    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.render(ordersList[position], OnShowOrderInfo)
    }

    override fun getItemCount(): Int = ordersList.size


    fun updateOrdersWithFilter(ordersList: List<OrderResult>){
        this.ordersList = ordersList.toMutableList()
        notifyDataSetChanged()
    }
}