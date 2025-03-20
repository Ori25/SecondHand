package com.example.androidproject

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApp : Application() {
    companion object {

        fun getSecondHandCategories(): List<String> {
            return listOf(
                "Electronics",
                "Furniture",
                "Clothing",
                "Books",
                "Toys",
                "Sports Equipment",
                "Home Appliances",
                "Jewelry",
                "Musical Instruments",
                "Automotive",
                "Baby Products",
                "Outdoor Gear",
                "Office Supplies",
                "Antiques",
                "Collectibles"
            )
        }
    }
}
