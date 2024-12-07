package com.example.myapplication.manager

import com.example.myapplication.model.User

class Session {
    private var _currentLogin: User? = null
    val currentLogin
        get() = _currentLogin
    fun login(user: User) {
        _currentLogin = user
    }
    fun logout() {
        _currentLogin = null
    }
    companion object {
        val get: Session by lazy {
            Session()
        }

    }
}