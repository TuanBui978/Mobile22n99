package com.example.myapplication.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp

data class Order(
    var id: String? = null,
    val item: Item? = null,
    val uid: String? = null,
    val status: EnumStatus? = null,
    val createAt: Timestamp? = null,
    @ServerTimestamp val updateAt: Timestamp? = null) {
    companion object {
        const val COLLECTION_PATH = "orders"
    }
}