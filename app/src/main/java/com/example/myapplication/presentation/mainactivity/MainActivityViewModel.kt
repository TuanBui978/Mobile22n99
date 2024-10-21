package com.example.myapplication.presentation.mainactivity

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
import com.example.myapplication.repository.UserRepositoryImp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivityViewModel(private val application: MyApplication, private val userRepository: UserRepository): ViewModel() {



    private var _currentUser = MutableLiveData<User>()
    val currentUser
        get() = _currentUser

    private var _status = MutableLiveData<InternetResult<User>>()
    val status
        get() = _status

    fun getCurrentUser() {
        _status.postValue(InternetResult.Loading)
        viewModelScope.launch {
            delay(2000)
            _status.postValue(userRepository.getCurrentUser())
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory = object :  ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val userRepository = checkNotNull(application.userRepository)
                return MainActivityViewModel(application, userRepository) as T
            }
        }
    }
}