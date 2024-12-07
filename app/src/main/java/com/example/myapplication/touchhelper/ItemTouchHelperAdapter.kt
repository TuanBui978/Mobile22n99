package com.example.myapplication.touchhelper

import android.icu.text.Transliterator.Position
import androidx.recyclerview.widget.ItemTouchHelper

interface ItemTouchHelperAdapter {
    fun onItemSwipe(position: Int)
}