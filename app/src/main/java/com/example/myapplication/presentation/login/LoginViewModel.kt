package com.example.myapplication.presentation.login

import android.app.Application
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val application: MyApplication): ViewModel() {

    private val userRepository: UserRepository = application.userRepository

    private var _status = MutableLiveData<InternetResult<User>>()
    val status
        get() = _status

    fun signIn(email:String, password: String) {
        _status.postValue(InternetResult.Loading)
        val context = application.applicationContext
        viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(userRepository.signIn(context, email, password))
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                return LoginViewModel(application) as T
            }
        }
    }
}