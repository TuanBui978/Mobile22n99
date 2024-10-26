package com.example.myapplication.presentation.admin.allitem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Item
import com.example.myapplication.presentation.admin.allorder.AdAllOrderViewModel
import com.example.myapplication.repository.ItemRepository
import kotlinx.coroutines.launch

class AdAllItemViewModel(private val application: MyApplication, private val itemRepository: ItemRepository): ViewModel() {
    private var _itemStatus = MutableLiveData<InternetResult<List<Item>>>()
    val itemStatus
        get() = _itemStatus
    fun getAllItem() {
        _itemStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _itemStatus.postValue(itemRepository.getAllItems())
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val itemRepository = application.itemRepository
                return AdAllItemViewModel(application, itemRepository) as T
            }
        }
    }

}