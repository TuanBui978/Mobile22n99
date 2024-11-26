package com.example.myapplication.repository

import com.example.myapplication.model.CartProduct
import com.example.myapplication.model.InternetResult

interface CartProductRepository {
    suspend fun addProductToCart(cartProduct: CartProduct): InternetResult<CartProduct>
    suspend fun getAllProductsInCart(userId: String): InternetResult<List<CartProduct>>
    suspend fun removeProductFromCart(cartProductId: String): InternetResult<Void>
    suspend fun removeProductsFromCart(cartProductIds: List<String>): InternetResult<Void>
    suspend fun updateProductQuantity(cartProductId: String, quantity: Int): InternetResult<Void>
    suspend fun getCartProductOfUserById(cartProductId: String, uid: String): InternetResult<CartProduct>
}