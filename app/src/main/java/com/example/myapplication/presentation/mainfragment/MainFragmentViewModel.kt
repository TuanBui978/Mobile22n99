package com.example.myapplication.presentation.mainfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.repository.UserRepository

class MainFragmentViewModel(private val application: MyApplication, private val userRepository: UserRepository) : ViewModel() {

    fun signOut() {
        userRepository.signOut()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val userRepository = checkNotNull(application.userRepository)
                return MainFragmentViewModel(application, userRepository) as T
            }
        }

    }
}
