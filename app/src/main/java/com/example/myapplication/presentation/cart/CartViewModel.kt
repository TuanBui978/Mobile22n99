package com.example.myapplication.presentation.cart

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.CartProduct
import com.example.myapplication.model.InternetResult
import com.example.myapplication.repository.CartProductRepository
import com.example.myapplication.repository.ProductRepository
import kotlinx.coroutines.launch

class CartViewModel(private val application: MyApplication, private val cartProductRepository: CartProductRepository, private val productRepository: ProductRepository): ViewModel() {

    private var _cartProductStatus = MutableLiveData<InternetResult<List<CartProduct>>>()
    val cartProductStatus
        get() = _cartProductStatus
    fun getListCartProduct(userId: String) {
        _cartProductStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _cartProductStatus.postValue(cartProductRepository.getAllProductsInCart(userId))
        }
    }

    fun createCartProductIdsBundle(list: List<String>) : Bundle {
        return bundleOf("cartProductIds" to list)
    }
    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val cartProductRepository = application.cartProductRepository
                val productRepository = application.productRepository
                return CartViewModel(application, cartProductRepository, productRepository) as T
            }
        }
    }

}