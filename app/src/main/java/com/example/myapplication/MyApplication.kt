package com.example.myapplication

import android.app.Application
import android.content.Context
import com.example.myapplication.repository.ItemRepository
//import com.example.myapplication.repository.ItemRepositoryImp
import com.example.myapplication.repository.OrderRepository
import com.example.myapplication.repository.OrderRepositoryImp
import com.example.myapplication.repository.ProductRepository
import com.example.myapplication.repository.ProductRepositoryImp
import com.example.myapplication.repository.ShopRepository
import com.example.myapplication.repository.ShopRepositoryImp
import com.example.myapplication.repository.UserRepository
import com.example.myapplication.repository.UserRepositoryImp

class MyApplication : Application() {
    private var _userRepository: UserRepository? = null
    private var _shopRepository: ShopRepository? = null
    private var _productRepository: ProductRepository? = null
    private var _orderRepository: OrderRepository? = null
    val userRepository
        get() = _userRepository!!

    val shopRepository
        get() = _shopRepository!!
    val productRepository
        get() = _productRepository!!
    val orderRepository
        get() = _orderRepository!!
    override fun onCreate() {
        super.onCreate()
        _userRepository = UserRepositoryImp.getInstance()
        _shopRepository = ShopRepositoryImp.getInstance()
        _orderRepository = OrderRepositoryImp.getInstance()
//        _itemRepository = ItemRepositoryImp.getInstance()
        _productRepository = ProductRepositoryImp.getInstance()
    }
}