package com.example.saturninaapp.viewholder

import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.ResultComment

class CommentsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val tvUserNameCommentary: TextView = view.findViewById(R.id.tvUserNameCommentary)
    private val tvUserCommentaryData: TextView = view.findViewById(R.id.tvUserCommentaryData)
    private val rbUserCommentaryRating: RatingBar = view.findViewById(R.id.rbUserCommentaryRating)

    fun render(comment: ResultComment){
        tvUserNameCommentary.text = comment.user_id.nombre + " " + comment.user_id.apellido
        tvUserCommentaryData.text = comment.descripcion
        setRatingBarValue(rbUserCommentaryRating, comment)

    }

    private fun setRatingBarValue(ratingBar: RatingBar, comment: ResultComment){
        ratingBar.rating = comment.calificacion.toFloat()
    }


}