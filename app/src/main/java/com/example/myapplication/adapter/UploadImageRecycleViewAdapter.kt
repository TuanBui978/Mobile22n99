package com.example.myapplication.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.databinding.SimpleImageRecyclerViewItemBinding

class UploadImageRecycleViewAdapter(private var images: MutableList<String> = mutableListOf()): Adapter<UploadImageRecycleViewAdapter.UploadImageViewHolder>() {


    class UploadImageViewHolder(val simpleImageRecyclerViewItemBinding: SimpleImageRecyclerViewItemBinding): ViewHolder(simpleImageRecyclerViewItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadImageViewHolder {
        val viewBinding = SimpleImageRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UploadImageViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: UploadImageViewHolder, position: Int) {
        holder.simpleImageRecyclerViewItemBinding.imageView.setImageURI(images[holder.adapterPosition].toUri())
        holder.simpleImageRecyclerViewItemBinding.closeButton.setOnClickListener{
            removeImage(holder.adapterPosition)
        }
    }
    fun addImages(images: List<String>) {
        val startPos = this.images.size
        this.images.addAll(images)
        notifyItemRangeInserted(startPos, images.size)
    }

    private fun removeImage(position: Int) {
        images.removeAt(position)
        notifyItemRemoved(position)
    }
}