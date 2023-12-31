package com.example.saturninaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.models.ResultComment
import com.example.saturninaapp.viewholder.CommentsViewHolder

class CommentsAdapter(private val commentsList: MutableList<ResultComment>)
    :RecyclerView.Adapter<CommentsViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_commentary, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.render(commentsList[position])
    }

    override fun getItemCount(): Int = commentsList.size



}