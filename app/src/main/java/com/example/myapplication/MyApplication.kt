package com.example.myapplication

import android.app.Application
import android.content.Context
import com.example.myapplication.repository.UserRepository
import com.example.myapplication.repository.UserRepositoryImp

class MyApplication : Application() {
    private var _userRepository: UserRepository? = null
    val userRepository
        get() = _userRepository!!

    override fun onCreate() {
        super.onCreate()
        _userRepository = UserRepositoryImp.getInstance()
    }

}