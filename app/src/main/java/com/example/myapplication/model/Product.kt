package com.example.myapplication.model

data class Product(
    var id: String,
    var name: String? = null,
    var type: EnumType? = null,
    var items: List<Item>? = null,
    var image: List<String>? = null,
    var price: Long? = null,
    var gender: EnumGenderType? = null,
    var description: String? = null
) {
    companion object {
        const val COLLECTION_PATH = "products"
    }
}
