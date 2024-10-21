package com.example.myapplication.presentation.signup

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User
import com.example.myapplication.repository.UserRepository
import com.example.myapplication.repository.UserRepositoryImp
import kotlinx.coroutines.launch

class SignUpViewModel(private val application: MyApplication, private val repository: UserRepository) : ViewModel() {

    private var _status = MutableLiveData<InternetResult<User>>()

    val status
        get() = _status
    fun signUp(email: String, password: String, retypePassword: String)  {
        Log.d("Mobile 22.99", "signUp: $password  $retypePassword" )
        _status.postValue(InternetResult.Loading)
        val context = application.applicationContext
        viewModelScope.launch {
            if (email.isBlank()) {
                status.postValue(InternetResult.Failed(Exception(context.getString(R.string.email_is_empty))))
                return@launch
            }
            if (password.isBlank()) {
                status.postValue(InternetResult.Failed(Exception(context.getString(R.string.password_is_empty))))
                return@launch
            }
            if (retypePassword.isBlank()) {
                status.postValue(InternetResult.Failed(Exception(context.getString(R.string.retype_password_is_empty))))
                return@launch
            }
            if (retypePassword != password) {
                status.postValue(InternetResult.Failed(Exception(context.getString(R.string.retype_not_equal_to_password))))
                return@launch
            }
            status.postValue(repository.signUp(context, email, password))
        }

    }

    companion object {
        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val userRepository = checkNotNull(application.userRepository)
                return SignUpViewModel(application, userRepository) as T
            }
        }
    }
}