package com.example.myapplication.presentation.listitem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product
import com.example.myapplication.presentation.admin.AdminViewModel
import com.example.myapplication.repository.ProductRepository
import kotlinx.coroutines.launch

class ListItemViewModel(private val application: MyApplication,private val productRepository: ProductRepository): ViewModel() {

    private var _productStatus = MutableLiveData<InternetResult<List<Product>>>()

    val productStatus
        get() = _productStatus

    fun getAllProduct() {
        _productStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _productStatus.postValue(productRepository.getAllProducts())
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as MyApplication
                val itemRepository = application.productRepository
                return ListItemViewModel(application, itemRepository) as T
            }
        }
    }
}