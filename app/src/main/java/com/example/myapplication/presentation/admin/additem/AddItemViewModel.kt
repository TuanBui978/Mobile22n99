package com.example.myapplication.presentation.admin.additem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product
import com.example.myapplication.presentation.admin.allitem.AdAllItemViewModel
import com.example.myapplication.repository.ProductRepository
import kotlinx.coroutines.launch

class AddItemViewModel(private val application: MyApplication, private val productRepository: ProductRepository): ViewModel() {

    private var _productStatus = MutableLiveData<InternetResult<Product>>()

    private var _addStatus = MutableLiveData<InternetResult<Void>>()

    val addStatus
        get() = _addStatus

    fun getProductById(productId: String) {
        _productStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _productStatus.postValue(productRepository.getProductById(productId))
        }
    }

    fun addProduct(product: Product) {
        _addStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _addStatus.postValue(productRepository.createProduct(product))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as MyApplication
                val productRepository = application.productRepository
                return AddItemViewModel(application, productRepository) as T
            }
        }
    }
}