package com.example.myapplication.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.SimpleImageRecyclerViewItemBinding

class UploadImageRecycleViewAdapter(private var images: MutableList<String> = mutableListOf()): Adapter<UploadImageRecycleViewAdapter.UploadImageViewHolder>() {

    fun getImages() : MutableList<String> {
        return this.images
    }

    class UploadImageViewHolder(val simpleImageRecyclerViewItemBinding: SimpleImageRecyclerViewItemBinding): ViewHolder(simpleImageRecyclerViewItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadImageViewHolder {
        val viewBinding = SimpleImageRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UploadImageViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: UploadImageViewHolder, position: Int) {

        val loadingDrawable = CircularProgressDrawable(holder.itemView.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }
        Glide.with(holder.itemView.context).load(images[holder.adapterPosition]).centerCrop().error(R.drawable.error_image_photo_icon).placeholder(loadingDrawable).into(holder.simpleImageRecyclerViewItemBinding.imageView)

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
    companion object {
        const val LOCAL = 0
        const val API = 1
    }
}