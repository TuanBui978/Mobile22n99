package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Variant(var id: Int? = null,
                   var color: String? = "#FFFFFFFF",

                   var size: EnumSize? = null,
                   var count: Int? = null,
    ) : Parcelable {
}


