package com.example.myapplication.model

import kotlinx.serialization.Serializable

data class Item(var name: String,
                var type: Type,
                var color: String,
                var size: EnumSize,
                var count: Int,
                var price: Long)
