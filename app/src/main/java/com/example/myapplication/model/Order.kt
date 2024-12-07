package com.example.myapplication.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Order(
    var id: String? = null,
    var cartProducts: List<CartProduct>? = null,
    var user: User? = null,
    var status: EnumStatus? = null,
    var createAt: Timestamp? = null,
    @ServerTimestamp val updateAt: Timestamp? = null) {
    companion object {
        const val COLLECTION_PATH = "orders"
    }
}