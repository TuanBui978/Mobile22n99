package com.example.myapplication.adapter

import android.transition.CircularPropagation
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ShopItemBinding
import com.example.myapplication.model.Shop
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.logging.LoggingMXBean

class ShopRecycleViewAdapter(private var shops: List<Shop>): Adapter<ShopRecycleViewAdapter.ViewHolder>() {

    private var onClickListener: (Shop)->Unit = {}
    class ViewHolder(val viewBinding: ShopItemBinding): RecyclerView.ViewHolder(viewBinding.root) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ShopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }
    override fun getItemCount(): Int {
        return shops.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewBinding = holder.viewBinding
        val shop = shops[position]
        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        Glide.with(holder.itemView.context).load(shops[position].image).placeholder(circularProgressDrawable).error(R.drawable.error_image_photo_icon).into(viewBinding.shopImage)
        viewBinding.shopName.text = shop.name
        viewBinding.shopAddress.text = shop.address
        holder.itemView.setOnClickListener {
            this.onClickListener(shop)
        }
    }

    fun onItemClickListener(onClickListener: (Shop)->Unit) {
        this.onClickListener = onClickListener
    }
}