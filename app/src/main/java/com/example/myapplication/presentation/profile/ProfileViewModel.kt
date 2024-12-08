package com.example.myapplication.presentation.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Order
import com.example.myapplication.model.Shop
import com.example.myapplication.model.User
import com.example.myapplication.repository.OrderRepository
import com.example.myapplication.repository.ShopRepository
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val application: MyApplication,private val userRepository: UserRepository, private val orderRepository: OrderRepository): ViewModel() {
    private var _profileStatus = MutableLiveData<InternetResult<User>>()
    private var _updateStatus = MutableLiveData<InternetResult<Void>>()
    private var _currentUserStatus = MutableLiveData<InternetResult<User>>()
    private var mOrderStatus = MutableLiveData<InternetResult<List<Order>>>()
    val profileStatus
        get() = _profileStatus
    val updateStatus
        get() = _updateStatus
    val currentUserStatus
        get() = _currentUserStatus
    val orderStatus
        get() = mOrderStatus

    fun getCurrentUser(context: Context, uid: String) {
        viewModelScope.launch {
            _currentUserStatus.postValue(userRepository.getUser(context, uid))
        }
    }

    fun getProfile(uid: String) {
        _profileStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _profileStatus.postValue(userRepository.getUser(application.applicationContext, uid))
        }
    }

    fun getNumOfConfirmingItem(uid: String) {
        getListOrderById(uid)
    }

    fun createOrUpdateProfile(user: User) {
        _updateStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _updateStatus.postValue(userRepository.updateUser(application.applicationContext, user))
        }
    }

    fun getListOrderById(uid: String) {
        mOrderStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            mOrderStatus.postValue(orderRepository.getOrderByUid(uid))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val userRepository = checkNotNull(application.userRepository)
                val orderRepository = checkNotNull(application.orderRepository)
                return ProfileViewModel(application, userRepository, orderRepository) as T
            }
        }
    }
}