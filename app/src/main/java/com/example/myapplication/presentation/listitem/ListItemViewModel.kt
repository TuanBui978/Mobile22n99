package com.example.myapplication.presentation.listitem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.CartProduct
import com.example.myapplication.model.EnumGenderType
import com.example.myapplication.model.EnumType
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product
import com.example.myapplication.presentation.admin.AdminViewModel
import com.example.myapplication.repository.CartProductRepository
import com.example.myapplication.repository.ProductRepository
import kotlinx.coroutines.launch

class ListItemViewModel(private val application: MyApplication,private val productRepository: ProductRepository, private val cartProductRepository: CartProductRepository): ViewModel() {

    private var _productStatus = MutableLiveData<InternetResult<List<Product>>>()
    private var mUpdateStatus = MutableLiveData<InternetResult<CartProduct>>()


    val productStatus
        get() = _productStatus

    val updateStatus
        get() = mUpdateStatus

    fun getAllProduct() {
        _productStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            val status = productRepository.getAllProducts()
            if (status != _productStatus) {
                _productStatus.postValue(status)
            }
        }
    }
    fun getProductBy(type: EnumType, gender: EnumGenderType) {
        _productStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            val status = productRepository.getProductBy(type, gender)
            if (status != _productStatus) {
                _productStatus.postValue(status)
            }
        }
    }

    fun addToCart(cartProduct: CartProduct) {
        mUpdateStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            mUpdateStatus.postValue(cartProductRepository.addProductToCart(cartProduct))
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as MyApplication
                val itemRepository = application.productRepository
                val cartProductRepository = application.cartProductRepository
                return ListItemViewModel(application, itemRepository, cartProductRepository) as T
            }
        }
    }
}