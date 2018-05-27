package com.epishie.spacial.ui.extensions

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun ImageView.loadImageFromUrl(url: String?) {
    if (url != null) {
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .into(this)
    }
}