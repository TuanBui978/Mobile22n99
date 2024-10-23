package com.example.myapplication.repository

import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Shop
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ShopRepositoryImp: ShopRepository {
    private val database = Firebase.firestore
    override suspend fun createShop(shop: Shop): InternetResult<Void> {
        return try {
            val result = database.collection("shops").document()
            shop.shopId = result.id
            result.set(shop).await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun getShopById(shopId: String): InternetResult<Shop> {
        return try {
            val result = database.collection("shops").document(shopId).get().await()
            val shop = result.toObject(Shop::class.java)
            if (shop != null) {
                InternetResult.Success(shop)
            }
            else {
                InternetResult.Failed(Exception("Result with empty value"))
            }
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun getShopsByAccountId(accountId: String): InternetResult<List<Shop>> {
        return  try {
            val result = database.collection("shops").whereEqualTo("accountId", accountId).get().await()
            val shops = result.toObjects(Shop::class.java)
            InternetResult.Success(shops)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun updateShop(shop: Shop): InternetResult<Void> {
        return try {
            database.collection("shops").document(shop.shopId!!).set(shop, SetOptions.merge()).await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun deleteShop(shopId: String): InternetResult<Void> {
        return try {
            database.collection("shops").document(shopId).delete().await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    companion object {
        private var shopRepositoryImp: ShopRepositoryImp? = null
        fun getInstance(): ShopRepository? {
            if (shopRepositoryImp==null) {
                shopRepositoryImp = ShopRepositoryImp()
            }
            return shopRepositoryImp!!
        }
    }
}