package com.example.myapplication.presentation.admin

import android.text.Editable.Factory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.myapplication.MyApplication
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Item
import com.example.myapplication.model.Order
import com.example.myapplication.model.Product
import com.example.myapplication.repository.ItemRepository
import com.example.myapplication.repository.OrderRepository
import com.example.myapplication.repository.ProductRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val application: MyApplication, private val productRepository: ProductRepository, private val orderRepository: OrderRepository): ViewModel() {

    private var _productStatus = MutableLiveData<InternetResult<List<Product>>>()
    private var _orderStatus = MutableLiveData<InternetResult<List<Order>>>()
    private var _deleteProductStatus = MutableLiveData<InternetResult<Void>>()

    val productStatus
        get() = _productStatus
    val orderStatus
        get() = _orderStatus
    val deleteProductStatus
        get() = _deleteProductStatus


    fun deleteItemWithId(id: String) {
        _deleteProductStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _deleteProductStatus.postValue(productRepository.deleteProduct(id))
        }
    }

    fun getOrderWithLimit(limit: Long) {
        _orderStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _orderStatus.postValue(orderRepository.getOrderWithLimit(limit))
        }
    }

    fun getItemWithLimit(limit: Long) {
        _productStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _productStatus.postValue(productRepository.getProductWithLimit(limit))
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val itemRepository = application.productRepository
                val orderRepository = application.orderRepository
                return AdminViewModel(application, itemRepository, orderRepository) as T
            }
        }
    }

}