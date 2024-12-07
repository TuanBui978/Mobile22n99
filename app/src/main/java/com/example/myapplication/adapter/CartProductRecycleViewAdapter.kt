package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.R
import com.example.myapplication.databinding.CartProductItemBinding
import com.example.myapplication.helper.loadImageIntoImageView
import com.example.myapplication.model.CartProduct
import com.example.myapplication.touchhelper.ItemTouchHelperAdapter

class CartProductRecycleViewAdapter(private var cartProducts: MutableList<CartProduct> = mutableListOf()):
    Adapter<CartProductRecycleViewAdapter.CartProductViewHolder>(), ItemTouchHelperAdapter {
    private var onItemClickListener: (CartProduct)->Unit = {}
    private var onItemDelete: (CartProduct, Int)->Unit = {_,_->}
    private var mTotalPrice = MutableLiveData<Float>().apply {
        var totalPrice = 0f
        cartProducts.forEach {
            val product = it.product
            val count = it.count
            totalPrice += product!!.price!! * count!!
        }
        this.postValue(totalPrice)
    }
    private var mItemTouchHelper: ItemTouchHelper? = null
    var itemTouchHelper: ItemTouchHelper?
        set(value) {
            mItemTouchHelper = value
        }
        get() = mItemTouchHelper



    @SuppressLint("ClickableViewAccessibility")
    inner class CartProductViewHolder constructor(private val viewBinding: CartProductItemBinding):
        ViewHolder(viewBinding.root),
        GestureDetector.OnGestureListener,
        View.OnTouchListener
        {
        private var mGestureDetector: GestureDetector = GestureDetector(viewBinding.root.context, this)
        init {
            viewBinding.root.setOnTouchListener(this)
        }

        fun bind(cartProduct: CartProduct) {
            val product = cartProduct.product!!
            val context = itemView.context
            loadImageIntoImageView(itemView.context, viewBinding.mainImage, product.mainImage!!.toUri())
            val variant = product.variants.first { it.id == cartProduct.variantId }
            val size = variant.size!!.display
            val color = variant.color!!
            val price = product.price!!
            val count = cartProduct.count!!
            val drawable = TextViewCompat.getCompoundDrawablesRelative(viewBinding.colorTextView)[2]
            drawable.setTint(Color.parseColor(color))
            viewBinding.colorTextView.setCompoundDrawables(null, null, drawable, null)
            viewBinding.sizeTextView.text = context.getString(R.string.size_selected, size)
            viewBinding.productNameTextView.text = product.name!!
            viewBinding.countTextView.text = count.toString()
            viewBinding.priceTextView.text = context.getString(R.string.price_display, price)
            viewBinding.totalPriceTextView.text = context.getString(R.string.price_display, price*count)
            viewBinding.addButton.setOnClickListener {
                cartProduct.count = cartProduct.count!! + 1
                viewBinding.countTextView.text = cartProduct.count.toString()
                viewBinding.totalPriceTextView.text = context.getString(R.string.price_display, price * cartProduct.count!!)
                mTotalPrice.postValue(mTotalPrice.value!! + price)
            }
            viewBinding.removeButton.setOnClickListener {
                cartProduct.count = if (cartProduct.count!! - 1 < 0) {
                    0
                }
                else {
                    mTotalPrice.postValue(mTotalPrice.value!! - price)
                    cartProduct.count!! - 1
                }
                viewBinding.countTextView.text = cartProduct.count.toString()
                viewBinding.totalPriceTextView.text = context.getString(R.string.price_display, price * cartProduct.count!!)
            }
        }
        override fun onDown(e: MotionEvent): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent) {

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return false
        }
        override fun onLongPress(e: MotionEvent) {
            mItemTouchHelper?.startDrag(this)
        }
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }

        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            v?.performClick()
            mGestureDetector.onTouchEvent(event)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        val viewBinding = CartProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartProductViewHolder(viewBinding)
    }
    override fun getItemCount(): Int {
        return cartProducts.size
    }
    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        holder.bind(cartProducts[holder.adapterPosition])
        holder.itemView.setOnClickListener {
            onItemClickListener(cartProducts[holder.adapterPosition])
        }
    }
    fun setOnItemClickListener(onItemClickListener: (CartProduct)->Unit = {}) {
        this.onItemClickListener = onItemClickListener
    }
    fun getTotalPrice(): MutableLiveData<Float> {
        return  mTotalPrice
    }
    fun getProducts(): Array<CartProduct> {
        return cartProducts.toTypedArray()
    }
    override fun onItemSwipe(position: Int) {
        val cartProduct = cartProducts[position]
        onItemDelete(cartProduct, position)
    }
    fun deleteItem(pos: Int) {
        cartProducts.removeAt(pos)
        notifyItemRemoved(pos)
        updatePrice()
    }
    fun setOnDelete(onDelete: (CartProduct, Int)->Unit = {_,_->}) {
        this.onItemDelete = onDelete
    }


    private fun updatePrice() {
        var totalPrice = 0f
        cartProducts.forEach {
            val product = it.product
            val count = it.count
            totalPrice += product!!.price!! * count!!
        }
        mTotalPrice.postValue(totalPrice)
    }
}