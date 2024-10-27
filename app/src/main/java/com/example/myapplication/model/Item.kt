package com.example.myapplication.model

import kotlinx.serialization.Serializable


data class Item(var color: String? = null,
                var size: EnumSize? = null,
                var count: Int? = null,
    ) {
}


