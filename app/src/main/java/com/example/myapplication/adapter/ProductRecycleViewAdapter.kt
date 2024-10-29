package com.example.myapplication.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ProductItemBinding
import com.example.myapplication.model.Product

class ProductRecycleViewAdapter(private var products: List<Product> = listOf()): Adapter<ProductRecycleViewAdapter.ViewHolder>() {

    private var onClickListener: (Product)->Unit = {}
    class ViewHolder(val viewBinding: ProductItemBinding): RecyclerView.ViewHolder(viewBinding.root) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }
    override fun getItemCount(): Int {
        return products.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onClickListener(products[holder.adapterPosition])
        }
        holder.viewBinding.itemName.text = products[holder.adapterPosition].name
        val colors = mutableListOf<String>()
        for (i in products[holder.adapterPosition].items) {
            colors.add(i.color!!)
        }
        val adapter = ColorRecycleViewAdapter(colors)
        holder.viewBinding.colorRecycleView.adapter = adapter
        val loadingDrawable = CircularProgressDrawable(holder.itemView.context)
        Glide.with(holder.itemView).load(products[holder.adapterPosition].mainImage!!.toUri()).centerCrop().placeholder(loadingDrawable).error(R.drawable.error_image_photo_icon).into(holder.viewBinding.itemImage)

    }

    fun onItemClickListener(onClickListener: (Product)->Unit) {
        this.onClickListener = onClickListener
    }
}