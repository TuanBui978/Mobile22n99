package com.example.myapplication.model

import android.location.Address

data class Shop(
    var shopId: String? = null,
    var name: String,
    var address: String,
    var accountId: String,
    var image: String
    )
