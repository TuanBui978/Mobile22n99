package com.example.myapplication.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.databinding.DetailColorItemBinding

class DetailColorAdapter(private val colors: MutableList<String>): Adapter<DetailColorAdapter.ColorViewHolder>() {
    private var selectedImage: Int = 0
    //    private var previousSelected: Int = -1
    private var onImageClickListener: (image:String)->Unit = {}

    fun setOnColorClickListener(onClickListener: (image:String)->Unit) {
        this.onImageClickListener = onClickListener
    }
    inner class ColorViewHolder(private val viewBinding: DetailColorItemBinding): ViewHolder(viewBinding.root) {
        fun bind(color: String, position: Int) {
            val imageView = viewBinding.color
            imageView.setColorFilter(Color.parseColor(color))
            if (adapterPosition == selectedImage) {
                onSelected()
            }
            else {
                onDefault()
            }
        }
        private fun onSelected() {
            val selected = viewBinding.selected
            selected.visibility = View.VISIBLE
        }
        private fun onDefault() {
            val selected = viewBinding.selected
            selected.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val viewBinding = DetailColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[holder.adapterPosition], holder.adapterPosition)
        holder.itemView.setOnClickListener {
            val previousSelected = selectedImage
            selectedImage = holder.adapterPosition
            onImageClickListener(colors[holder.adapterPosition])
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedImage)
        }
    }

    fun getSelectedColor(): String {
        return colors[selectedImage]
    }
}