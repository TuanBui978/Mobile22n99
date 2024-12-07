package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    var id: String? = null,
    var uid: String? = null,
    var product: Product? = null,
    var variantId: Int? = null,
    var count: Int? = null,
    var status: EnumStatus? = EnumStatus.Confirming
) : Parcelable {
    companion object {
        const val DOCUMENT_PATH = "cartProduct"
    }
}