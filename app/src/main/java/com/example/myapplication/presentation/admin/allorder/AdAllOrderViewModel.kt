package com.example.myapplication.presentation.admin.allorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Order
import com.example.myapplication.repository.OrderRepository
import kotlinx.coroutines.launch

class AdAllOrderViewModel(private val application: MyApplication, private val orderRepository: OrderRepository): ViewModel() {
    private var _Post_orderStatus = MutableLiveData<InternetResult<List<Order>>>()
    val orderStatus
        get() = _Post_orderStatus
    fun getAllOrder() {
        _Post_orderStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _Post_orderStatus.postValue(orderRepository.getAllOrder())
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