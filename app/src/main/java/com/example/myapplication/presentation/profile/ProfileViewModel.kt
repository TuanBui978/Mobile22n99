package com.example.myapplication.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val application: MyApplication,private val userRepository: UserRepository): ViewModel() {

    private var _profileStatus = MutableLiveData<InternetResult<User>>()
    private var _updateStatus = MutableLiveData<InternetResult<Void>>()
    private var _currentUserStatus = MutableLiveData<InternetResult<User>>()
    val profileStatus
        get() = _profileStatus
    val updateStatus
        get() = _updateStatus
    val currentUserStatus
        get() = _currentUserStatus

    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUserStatus.postValue(userRepository.getCurrentUser())
        }
    }

    fun getProfile(uid: String) {
        _profileStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _profileStatus.postValue(userRepository.getUser(application.applicationContext, uid))
        }
    }

    fun createOrUpdateProfile(user: User) {
        _updateStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _updateStatus.postValue(userRepository.updateUser(application.applicationContext, user))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val userRepository = checkNotNull(application.userRepository)
                return ProfileViewModel(application, userRepository) as T
            }
        }
    }
}