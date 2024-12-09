package com.example.myapplication.presentation.admin.allitem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.MyApplication
import com.example.myapplication.model.EnumGenderType
import com.example.myapplication.model.EnumType
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product
import com.example.myapplication.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class AdAllItemViewModel(private val application: MyApplication, private val productRepository: ProductRepository): ViewModel() {
    private var _productStatus = MutableLiveData<InternetResult<List<Product>>>()
    private var _deleteProductStatus = MutableLiveData<InternetResult<Void>>()
    private var job: Job? = null

    val deleteProduct
        get() = _deleteProductStatus
    val productStatus
        get() = _productStatus
    private var mProducts = MutableLiveData<List<Product>>()
    val products
        get() = mProducts
    fun getAllItem() {
        _productStatus.postValue(InternetResult.Loading)
        job?.cancel()
        job = viewModelScope.launch {
            _productStatus.postValue(productRepository.getAllProducts())
        }
    }

    fun deleteItemWithId(id: String) {
        _deleteProductStatus.postValue(InternetResult.Loading)
        viewModelScope.launch {
            _deleteProductStatus.postValue(productRepository.deleteProduct(id))
        }
    }

    fun getProductByTypeAndGender(type: EnumType?, genderType: EnumGenderType?) {
        _productStatus.postValue(InternetResult.Loading)
        job?.cancel()
        viewModelScope.launch {
            _productStatus.postValue(productRepository.getProductByTypeAndGender(type, genderType))
        }
    }
//    fun getItemByType
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApplication
                val productRepository = application.productRepository
                return AdAllItemViewModel(application, productRepository) as T
            }
        }
    }

}