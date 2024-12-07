package com.example.myapplication.presentation.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.CartProduct
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product
import com.example.myapplication.repository.CartProductRepository
import com.example.myapplication.repository.ProductRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val application: MyApplication, private val productRepository: ProductRepository, private val cartProductRepository: CartProductRepository): ViewModel() {

    private val _productStatus = MutableLiveData<InternetResult<Product>>()
    private val _createCartProductStatus = MutableLiveData<InternetResult<CartProduct>>()
    private val _createAndPayCartProductStatus = MutableLiveData<InternetResult<CartProduct>>()
    val productStatus
        get() = _productStatus
    val createCartProductStatus
        get() = _createCartProductStatus
    val createAndPayCartProductStatus
        get() = _createAndPayCartProductStatus
    fun getProductById(id: String) {
        _productStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _productStatus.postValue(productRepository.getProductById(id))
        }
    }

    fun createCartProduct(cartProduct: CartProduct) {
        _createCartProductStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _createCartProductStatus.postValue(cartProductRepository.addProductToCart(cartProduct))
        }
    }




    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val productRepository = application.productRepository
                val cartProductRepository = application.cartProductRepository
                return DetailViewModel(application, productRepository, cartProductRepository) as T
            }
        }
    }
}

