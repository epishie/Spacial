package com.epishie.spacial.ui.widget

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class SpaceGridItemDecoration(private val space: Int,
                              private val spanCount: Int,
                              private val orientation: Int)
    : RecyclerView.ItemDecoration() {
    companion object {
        const val VERTICAL = 0
        const val HORIZONTAL = 1
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (orientation == VERTICAL) {
            outRect.right = space
            outRect.bottom = space
            if (position % spanCount == 0) {
                outRect.left = space
            }
            if (position < spanCount) {
                outRect.top = space
            }
        }
        // TODO Horizontal
    }
}