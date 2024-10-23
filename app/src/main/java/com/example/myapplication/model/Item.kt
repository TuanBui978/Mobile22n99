package com.example.myapplication.model

import kotlinx.serialization.Serializable

<<<<<<< Updated upstream
data class Item(var name: String,
                var type: EnumType,
                var color: String,
                var size: EnumSize,
                var count: Int,
                var price: Long)
=======
data class Item(var id: String? = null,
                var name: String? = null,
                var type: EnumType? = null,
                var color: String? = null,
                var size: EnumSize? = null,
                var count: Int? = null,
                var price: Long? = null,
                var shopId: String? = null,
                var shopName: String? = null,
                var image: List<String>? = null,
                var gender: EnumGenderType? = null) {
    companion object {
        const val COLLECTION_PATH = "items"
    }
}
>>>>>>> Stashed changes
