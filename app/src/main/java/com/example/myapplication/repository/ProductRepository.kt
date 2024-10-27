package com.example.myapplication.repository

import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Product

interface ProductRepository {
    // Thêm một Product mới
    suspend fun createProduct(product: Product): InternetResult<Void>

    // Lấy Product theo ID
    suspend fun getProductById(productId: String): InternetResult<Product>

    suspend fun getProductWithLimit(limit: Long): InternetResult<List<Product>>

    // Lấy tất cả các Product
    suspend fun getAllProducts(): InternetResult<List<Product>>

    // Cập nhật Product
    suspend fun updateProduct(product: Product): InternetResult<Void>

    // Xóa Product
    suspend fun deleteProduct(productId: String): InternetResult<Void>


}