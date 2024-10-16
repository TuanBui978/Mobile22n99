package com.example.myapplication.presentation.mainfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication

class MainFragmentViewModel(private val application: MyApplication) : ViewModel() {

    private val userRepository = application.userRepository


    fun signOut() {
        userRepository.signOut()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                return MainFragmentViewModel(application) as T
            }
        }

    }
}
