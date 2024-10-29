package com.example.myapplication.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.example.myapplication.databinding.ColorItemBinding

class ColorRecycleViewAdapter(private var colors: MutableList<String> = mutableListOf()): Adapter<ColorRecycleViewAdapter.ColorViewHolder>() {
    class ColorViewHolder(val viewBinding: ColorItemBinding): ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val viewBinding = ColorItemBinding.inflate(LayoutInflater.from(parent.context))
        return ColorViewHolder(viewBinding)
    }
    override fun getItemCount(): Int {
        return colors.size
    }
    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.viewBinding.color.setColorFilter(Color.parseColor(colors[holder.adapterPosition]))
    }
}