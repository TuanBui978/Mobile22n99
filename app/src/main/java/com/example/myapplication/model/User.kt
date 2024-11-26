package com.example.myapplication.model

import com.google.firebase.auth.FirebaseUser

data class User (var uid: String? = null,
                 var email: String? = null,
                 var cartProducts: List<CartProduct>? = null,
                 var address: String? = null,
                 var phoneNumber: String? = null,
                 var gender: String? = null,
                 var avatar: String? = null
    ) {
    constructor(user: FirebaseUser): this(user.uid, user.email)
    override fun toString(): String {
        return "User{" +
                "id='" + uid + '\'' +
                ", email='" + email + '\'' +
                '}'
    }
    override fun equals(other: Any?): Boolean {
        return if (other !is User) {
            false
        }
        else {
            this.uid == other.uid
        }
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}