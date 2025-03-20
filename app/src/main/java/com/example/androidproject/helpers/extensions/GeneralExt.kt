package com.example.androidproject.helpers.extensions

import android.R
import android.content.Intent
import android.net.Uri
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.snackbar.Snackbar
import java.net.MalformedURLException
import java.net.URL


fun String.isValidUrl(): Boolean {
    return try {
        URL(this)
        true // URL is valid
    } catch (e: MalformedURLException) {
        false // URL is invalid
    }
}


fun ListView.setupLinksAdapter(links: List<String>, openActivityCallback: (Intent) -> Unit) {
    val adapter =
        ArrayAdapter(context, R.layout.simple_list_item_1, links)

    this.adapter = adapter

    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, selectedIndex, _ ->
            with(links[selectedIndex]) {
                if (isValidUrl()) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(this)
                    openActivityCallback(intent)
                } else {
                    Snackbar.make(
                        this@setupLinksAdapter,
                        "Could not open link, broken",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
}

