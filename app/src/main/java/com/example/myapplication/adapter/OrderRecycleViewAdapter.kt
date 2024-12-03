package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.R
import com.example.myapplication.databinding.OrderItemBinding
import com.example.myapplication.helper.loadImageIntoImageView
import com.example.myapplication.model.EnumStatus
import com.example.myapplication.model.Order

class OrderRecycleViewAdapter(private var orders: List<Order> = listOf()):
    Adapter<OrderRecycleViewAdapter.OrderViewHolder>() {

    private var onStatusSelected: (Order)->Unit = {  }

    fun setOnStatusSelection(onStatusSelected: (Order)->Unit) {
        this.onStatusSelected = onStatusSelected
    }
    inner class OrderViewHolder(private val viewBinding: OrderItemBinding): ViewHolder(viewBinding.root) {
        fun bind(order: Order) {
            val user = order.user!!
            val products = order.cartProducts!!
            val context = itemView.context
            viewBinding.userName.text = user.email
            loadImageIntoImageView(context, viewBinding.userAvatar, user.avatar?.toUri())
            val adapter = ProductOfOrderRecycleViewAdapter(products)
            viewBinding.productRecycleView.adapter = adapter
            viewBinding.userAddress.text = user.address!!
            viewBinding.userPhoneNumber.text = user.phoneNumber
            val status = EnumStatus.entries
            val spinnerAdapter = ArrayAdapter(context, R.layout.order_status_spinner_item, status)
            spinnerAdapter.setDropDownViewResource(R.layout.order_status_drop_down_item)
            viewBinding.statusSpinner.adapter = spinnerAdapter
            status.forEachIndexed { index, enumStatus ->
                if (order.status == enumStatus) {
                    viewBinding.statusSpinner.setSelection(index)
                    return@forEachIndexed
                }
            }
            var isFirstLoad = true
            viewBinding.statusSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (isFirstLoad) {
                        isFirstLoad = false
                        return
                    }
                    order.status = status[position]
                    onStatusSelected(order)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val viewBinding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

}