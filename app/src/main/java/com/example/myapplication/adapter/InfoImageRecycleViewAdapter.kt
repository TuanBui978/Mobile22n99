package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.InfoImageItemBinding

class InfoImageRecycleViewAdapter(private var images: List<String>): Adapter<InfoImageRecycleViewAdapter.InfoImageViewHolder>() {
    private var selectedImage: Int = 0
//    private var previousSelected: Int = -1
    private var onImageClickListener: (image:String)->Unit = {}

    fun setOnImageClickListener(onClickListener: (image:String)->Unit) {
        this.onImageClickListener = onClickListener
    }
    inner class InfoImageViewHolder(private val viewBinding: InfoImageItemBinding): ViewHolder(viewBinding.root) {
        var isSelected = false
        fun bind(image: String, position: Int) {
            val imageView = viewBinding.root
            val circularProgressDrawable = CircularProgressDrawable(imageView.context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            }
            Glide.with(imageView.context).load(image).error(R.drawable.error_image_photo_icon).placeholder(circularProgressDrawable).into(imageView)
            if (adapterPosition == selectedImage) {
                onSelected()
            }
            else {
                onDefault()
            }
        }
        private fun onSelected() {
            val imageView = viewBinding.root
            imageView.foreground = AppCompatResources.getDrawable(imageView.context, R.drawable.line_conner)

        }
        private fun onDefault() {
            val imageView = viewBinding.root
            imageView.foreground = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoImageViewHolder {
        val viewBinding = InfoImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InfoImageViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: InfoImageViewHolder, position: Int) {
        holder.bind(images[holder.adapterPosition], holder.adapterPosition)
        holder.itemView.setOnClickListener {
            val previousSelected = selectedImage
            selectedImage = holder.adapterPosition
            onImageClickListener(images[holder.adapterPosition])
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedImage)
        }
    }
}