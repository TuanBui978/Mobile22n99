package com.example.myapplication.model

import kotlinx.serialization.Serializable

data class CartItem(
    var item: Item,
    var count: Int,
    var status: EnumStatus
) {
}