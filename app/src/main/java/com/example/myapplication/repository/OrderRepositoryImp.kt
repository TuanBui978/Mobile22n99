package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Order
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class OrderRepositoryImp: OrderRepository {
    private val database = Firebase.firestore
    override suspend fun getAllOrder(): InternetResult<List<Order>> {
        return try {
            val snapshot = database.collection(Order.COLLECTION_PATH).get().await()
            val orders = snapshot.toObjects(Order::class.java)
            InternetResult.Success(orders)
        }
        catch (e: Exception) {
            Log.e("OrderRepository", "Lấy đơn hàng thất bại", e)
            InternetResult.Failed(e)
        }
    }

    override suspend fun getOrderWithLimit(limit: Long): InternetResult<List<Order>> {
        return try {
            val snapshot = database.collection(Order.COLLECTION_PATH).limit(limit).get().await()
            val orders = snapshot.toObjects(Order::class.java)
            InternetResult.Success(orders)
        }
        catch (e: Exception) {
            Log.e("OrderRepository", "Lấy đơn hàng thất bại", e)
            InternetResult.Failed(e)
        }
    }


    override suspend fun getOrderById(orderId: String): InternetResult<Order> {
        return try {
            val snapshot = database.collection(Order.COLLECTION_PATH).document(orderId).get().await()
            val order = snapshot.toObject(Order::class.java)
            InternetResult.Success(order)
        }
        catch (e: Exception) {
            Log.e("OrderRepository", "Lấy đơn hàng thất bại", e)
            InternetResult.Failed(e)
        }
    }

    override suspend fun getOrderByUid(uid: String): InternetResult<List<Order>> {
        return try {
            val snapshot = database.collection(Order.COLLECTION_PATH).whereEqualTo("uid", uid).get().await()
            val orders = snapshot.toObjects(Order::class.java)
            InternetResult.Success(orders)
        }
        catch (e: Exception) {
            Log.e("OrderRepository", "Lấy đơn hàng thất bại", e)
            InternetResult.Failed(e)
        }
    }

    override suspend fun addOrder(order: Order): InternetResult<Void> {
        return try {
            val documentRef = database.collection(Order.COLLECTION_PATH).document()
            order.id = documentRef.id
            documentRef.set(order).await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            Log.e("OrderRepository", "Thêm đơn hàng thất bại", e)
            InternetResult.Failed(e)
        }
    }

    override suspend fun updateOrder(order: Order): InternetResult<Void> {
        return try {
            database.collection(Order.COLLECTION_PATH).document(order.id!!).set(order, SetOptions.merge()).await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            Log.e("OrderRepository", "Cập nhật đơn hàng thất bại", e)
            InternetResult.Failed(e)
        }
    }

    override suspend fun deleteOrder(orderId: String): InternetResult<Void> {
        return try {
            database.collection(Order.COLLECTION_PATH).document(orderId).delete().await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            Log.e("OrderRepository", "Xoá đơn hàng thất bại", e)
            InternetResult.Failed(e)
        }
    }

    companion object {
        private var orderRepository: OrderRepositoryImp? = null
        fun getInstance(): OrderRepositoryImp {
            if (orderRepository == null) {
                orderRepository = OrderRepositoryImp()
            }
            return orderRepository!!
        }
    }

}