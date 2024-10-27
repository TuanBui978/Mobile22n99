package com.example.myapplication.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemItemBinding
import com.example.myapplication.model.Item
import com.example.myapplication.model.Shop

class ItemRecycleViewAdapter(private var items: List<Item> = listOf()): Adapter<ItemRecycleViewAdapter.ViewHolder>() {

    private var onClickListener: (Item)->Unit = {}
    class ViewHolder(val viewBinding: ItemItemBinding): RecyclerView.ViewHolder(viewBinding.root) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }
    override fun getItemCount(): Int {
        return 10
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val viewBinding = holder.viewBinding
//        val item = items[position]
//        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
//        Glide.with(holder.itemView.context).load(items[position].image).placeholder(circularProgressDrawable).error(R.drawable.error_image_photo_icon).into(viewBinding.itemImage)
//        val view = View(holder.itemView.context)
//        val sizeInDp = 14
//        val sizeInPixels = (sizeInDp * holder.itemView.context.resources.displayMetrics.density).toInt()
//        val layoutParams = ViewGroup.LayoutParams(sizeInPixels, sizeInPixels)
//        view.layoutParams = layoutParams
//        view.setBackgroundColor(Color.GREEN)
//        viewBinding.colorLayout.addView(view)
//        viewBinding.itemName.text = item.name
//        holder.itemView.setOnClickListener {
//            this.onClickListener(item)
//        }
    }

    fun onItemClickListener(onClickListener: (Item)->Unit) {
        this.onClickListener = onClickListener
    }
}