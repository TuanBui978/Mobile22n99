package com.example.myapplication.model

data class Product(
    var id: String? = null,
    var name: String? = null,
    var type: EnumType? = EnumType.TShirt,
    var items: MutableList<Item> = mutableListOf(),
    var mainImage: String? = null,
    var images: MutableList<String> = mutableListOf(),
    var price: Float? = null,
    var gender: EnumGenderType? = EnumGenderType.MALE,
    var description: String? = null
) {
    companion object {
        const val COLLECTION_PATH = "products"
    }
}
