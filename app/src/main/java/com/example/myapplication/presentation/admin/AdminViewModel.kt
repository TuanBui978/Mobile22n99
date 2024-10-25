package com.example.myapplication.presentation.admin

import android.text.Editable.Factory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Item
import com.example.myapplication.model.Order
import com.example.myapplication.repository.ItemRepository
import com.example.myapplication.repository.OrderRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val application: MyApplication, private val itemRepository: ItemRepository, private val orderRepository: OrderRepository): ViewModel() {

    private var _itemStatus = MutableLiveData<InternetResult<List<Item>>>()
    private var _orderStatus = MutableLiveData<InternetResult<List<Order>>>()

    val itemStatus
        get() = _itemStatus
    val orderStatus
        get() = _orderStatus

    fun getOrderWithLimit(limit: Long) {
        _orderStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _orderStatus.postValue(orderRepository.getOrderWithLimit(limit))
        }
    }

    fun getItemWithLimit(limit: Long) {
        _itemStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _itemStatus.postValue(itemRepository.getItemWithLimit(limit))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val itemRepository = application.itemRepository
                val orderRepository = application.orderRepository
                return AdminViewModel(application, itemRepository, orderRepository) as T
            }
        }
    }

}