package com.example.myapplication.presentation.admin.allorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.EnumStatus
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Order
import com.example.myapplication.repository.OrderRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.Date

class AdAllOrderViewModel(private val application: MyApplication, private val orderRepository: OrderRepository): ViewModel() {
    private var _orderStatus = MutableLiveData<InternetResult<List<Order>>>()
    private var mUpdateOrderStatus = MutableLiveData<InternetResult<Void>>()
    val updateOrderStatus
        get() = mUpdateOrderStatus
    val orderStatus
        get() = _orderStatus
    fun getAllOrder() {
        _orderStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _orderStatus.postValue(orderRepository.getAllOrder())
        }
    }

    fun filterOrder(from: Timestamp?, to: Timestamp?, status: EnumStatus?) {
        _orderStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _orderStatus.postValue(orderRepository.getOrder(from, to, status))
        }
    }

    fun updateOrder(order: Order) {
        mUpdateOrderStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            mUpdateOrderStatus.postValue(orderRepository.updateOrder(order))
        }

    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as MyApplication
                val orderRepository = application.orderRepository
                return AdAllOrderViewModel(application, orderRepository) as T
            }
        }
    }
}