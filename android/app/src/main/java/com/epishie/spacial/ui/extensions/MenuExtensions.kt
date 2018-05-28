package com.epishie.spacial.ui.extensions

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.Menu
import com.epishie.spacial.R

fun Menu.tintIcons(context: Context) {
    tintIcons(context, R.color.colorOnSurface)
}

fun Menu.tintIcons(context: Context, @ColorRes color: Int) {
    for (i in 0 until size()) {
        val item = getItem(i)
        if (item.icon == null) {
            continue
        }
        val drawable = DrawableCompat.wrap(item.icon)
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, color))
        item.icon = drawable
    }
}