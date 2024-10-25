package com.example.myapplication.model

import kotlinx.serialization.Serializable


data class Item(var id: String? = null,
                var name: String? = null,
                var type: EnumType? = null,
                var color: String? = null,
                var size: EnumSize? = null,
                var count: Int? = null,
                var price: Long? = null,
                var image: List<String>? = null,
                var gender: EnumGenderType? = null) {
    companion object {
        const val COLLECTION_PATH = "items"
    }
}


