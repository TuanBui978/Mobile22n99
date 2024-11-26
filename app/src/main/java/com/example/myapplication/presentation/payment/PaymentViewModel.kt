package com.example.myapplication.presentation.payment

import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.EnumStatus
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Order
import com.example.myapplication.repository.CartProductRepository
import com.example.myapplication.repository.OrderRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class PaymentViewModel(private val application: MyApplication,private val cartProductRepository: CartProductRepository,private val orderRepository: OrderRepository): ViewModel() {
    private val _createOrderStatus = MutableLiveData<InternetResult<Void>>()
    private var mSelectedMethod = MutableLiveData<@receiver:IdRes Int?>()
    private var mDeleteCartProductStatus = MutableLiveData<InternetResult<Void>>()
    val selectedMethod
        get() = mSelectedMethod
    val createOrderStatus
        get() = _createOrderStatus
    val deleteCartProductStatus
        get() = mDeleteCartProductStatus
    fun createOrder(cartProducts: List<String>) {
//        _createOrderStatus.postValue(InternetResult.Loading)
//        viewModelScope.launch {
//            val order = Order(cartProductIds = cartProducts, status = EnumStatus.Confirming, createAt = Timestamp.now())
//            _createOrderStatus.postValue(orderRepository.addOrder(order = order))
//        }
    }

    fun deleteCartProducts(cartProducts: List<String>) {
        mDeleteCartProductStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            mDeleteCartProductStatus.postValue(cartProductRepository.removeProductsFromCart(cartProducts))
        }
    }

    fun selectMethod(@IdRes methodId: Int?){
        mSelectedMethod.postValue(methodId)
    }


    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as MyApplication
                val orderRepository = application.orderRepository
                val cartProductRepository = application.cartProductRepository
                return PaymentViewModel(application, cartProductRepository, orderRepository) as T
            }
        }
    }
}