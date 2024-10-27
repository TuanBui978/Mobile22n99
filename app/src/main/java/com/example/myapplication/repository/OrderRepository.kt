package com.example.myapplication.repository

import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Order

interface OrderRepository {

    // Lấy tất cả đơn hàng
    suspend fun getAllOrder(): InternetResult<List<Order>>

    // Lấy đơn hàng với số lượng tối đa
    suspend fun getOrderWithLimit(limit: Long) : InternetResult<List<Order>>

    // Lấy đơn hàng theo ID
    suspend fun getOrderById(orderId: String): InternetResult<Order>

    // Lấy đơn hàng theo uid
    suspend fun getOrderByUid(uid: String): InternetResult<List<Order>>

    // Thêm đơn hàng mới
    suspend fun addOrder(order: Order): InternetResult<Void>

    // Cập nhật đơn hàng
    suspend fun updateOrder(order: Order): InternetResult<Void>

    // Xóa đơn hàng theo ID
    suspend fun deleteOrder(orderId: String): InternetResult<Void>




}