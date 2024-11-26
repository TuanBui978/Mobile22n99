package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: String? = null,
    var name: String? = null,
    var type: EnumType? = EnumType.TShirt,
    var variants: MutableList<Variant> = mutableListOf(),
    var mainImage: String? = null,
    var images: MutableList<String> = mutableListOf(),
    var price: Float? = null,
    var gender: EnumGenderType? = EnumGenderType.MALE,
    var description: String? = null
) : Parcelable {
    companion object {
        const val COLLECTION_PATH = "products"
    }
}

