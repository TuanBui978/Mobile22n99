package com.example.myapplication.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemOfOrderBinding
import com.example.myapplication.databinding.ProductItemBinding
import com.example.myapplication.helper.loadImageIntoImageView
import com.example.myapplication.model.CartProduct

class ProductOfOrderRecycleViewAdapter(private var cartProducts: List<CartProduct>): Adapter<ProductOfOrderRecycleViewAdapter.ProductViewHolder>() {
    inner class ProductViewHolder(private val viewBinding: ItemOfOrderBinding): ViewHolder(viewBinding.root) {
        fun bind(cartProduct: CartProduct) {
            val product = cartProduct.product
            val context = itemView.context
            val variant = product!!.variants.first { it.id == cartProduct.variantId }
            loadImageIntoImageView(context, viewBinding.productImage, product.mainImage!!.toUri())
            viewBinding.productNameTextView.text = product.name
            viewBinding.sizeTextView.text = context.getString(R.string.size_selected, variant.size)
            viewBinding.color.setColorFilter(Color.parseColor(variant.color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val viewBinding = ItemOfOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return cartProducts.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(cartProducts[position])
    }
}