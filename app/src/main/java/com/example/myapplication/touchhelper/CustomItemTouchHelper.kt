package com.example.myapplication.touchhelper

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class CustomItemTouchHelper constructor(adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    private var mAdapter: ItemTouchHelperAdapter = adapter
    var adapter: ItemTouchHelperAdapter
        set(value) {
            mAdapter = value
        }
        get() = mAdapter

    override fun isItemViewSwipeEnabled(): Boolean {
        return true

    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
    }
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlag, swipeFlag)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemSwipe(viewHolder.layoutPosition)
    }

}