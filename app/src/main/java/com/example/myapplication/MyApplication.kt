package com.example.myapplication

import android.app.Application
import android.content.Context
import com.example.myapplication.repository.ShopRepository
import com.example.myapplication.repository.ShopRepositoryImp
import com.example.myapplication.repository.UserRepository
import com.example.myapplication.repository.UserRepositoryImp

class MyApplication : Application() {
    private var _userRepository: UserRepository? = null
    private var _shopRepository: ShopRepository? = null
    val userRepository
        get() = _userRepository!!

    val shopRepository
        get() = _shopRepository!!
    override fun onCreate() {
        super.onCreate()
        _userRepository = UserRepositoryImp.getInstance()
        _shopRepository = ShopRepositoryImp.getInstance()
    }
}