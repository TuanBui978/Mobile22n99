package com.example.myapplication.decoreration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.Orientation

class CustomDecoration(private val spanCount: Int = 1, val orientation: Int = GridLayoutManager.VERTICAL): ItemDecoration() {
    private var dimension: Rect = Rect()
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (orientation == GridLayoutManager.VERTICAL) {
                if (parent.getChildAdapterPosition(view) < spanCount) {
                    top = dimension.top
                }
                if (parent.getChildAdapterPosition(view) % spanCount == 0) {
                    left = dimension.bottom
                }
            } else {
                if (parent.getChildAdapterPosition(view) < spanCount) {
                    left = dimension.left
                }
                if (parent.getChildAdapterPosition(view) % spanCount == 0) {
                    top = dimension.top
                }
            }

            right = dimension.right
            bottom = dimension.bottom
        }
    }
    fun setDimension(left: Int, right: Int, top: Int, bottom: Int) {
        this.dimension = Rect(left, top, right, bottom)
    }
    fun setDimension(space: Int) {
        this.dimension = Rect(space, space, space, space)
    }
}