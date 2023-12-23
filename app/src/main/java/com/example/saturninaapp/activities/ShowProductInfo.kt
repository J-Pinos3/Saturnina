package com.example.saturninaapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.saturninaapp.R
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.ImageCarousel

class ShowProductInfo : AppCompatActivity() {

    private val imagesList = mutableListOf<CarouselItem>()
    private lateinit var icCarousel: ImageCarousel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_product_info)
        initUI()

        imagesList.add(
            CarouselItem(
                imageUrl = "https://cdn-p.smehost.net/sites/7f9737f2506941499994d771a29ad47a/wp-content/uploads/2021/03/Screen-Shot-2021-03-04-at-1.06.09-PM.png"
            )
        )

        imagesList.add(
            CarouselItem(
                imageUrl = "https://cdn.mos.cms.futurecdn.net/iRCQJTSmkYketgvNpSWYNM.jpg"
            )
        )

        icCarousel.addData(imagesList)


        //IMPROVE SPINNERS https://stackoverflow.com/questions/2927012/how-can-i-change-decrease-the-android-spinner-size
    }//ON CREATE



    private fun initUI() {
        //carousel of images
        icCarousel = findViewById(R.id.icCarousel)
    }


}