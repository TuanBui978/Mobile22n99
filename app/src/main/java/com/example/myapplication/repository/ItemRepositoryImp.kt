package com.example.myapplication.repository

import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Item
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ItemRepositoryImp: ItemRepository {
    private val database = Firebase.firestore
    override suspend fun createItem(item: Item): InternetResult<Void> {
        return try {
            val result = database.collection(Item.COLLECTION_PATH).document()
            item.id = result.id
            result.set(item).await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun getItemById(itemId: String): InternetResult<Item> {
        return try {
            val result = database.collection(Item.COLLECTION_PATH).document(itemId).get().await()
            val item = result.toObject(Item::class.java)
            if (item != null) {
                InternetResult.Success(item)
            }
            else {
                InternetResult.Failed(Exception("Result with empty value"))
            }
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun getItemsByShopId(shopId: String): InternetResult<List<Item>> {
        return try {
            val result = database.collection(Item.COLLECTION_PATH).whereEqualTo("shopId", shopId).get().await()
            val items = result.toObjects(Item::class.java)
            InternetResult.Success(items)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun getAllItems(): InternetResult<List<Item>> {
        return try {
            val result = database.collection(Item.COLLECTION_PATH).get().await()
            val items = result.toObjects(Item::class.java)
            InternetResult.Success(items)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun updateItem(item: Item): InternetResult<Void> {
        return try {
            database.collection(Item.COLLECTION_PATH).document(item.id!!).set(item, SetOptions.merge()).await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun deleteItem(itemId: String): InternetResult<Void> {
        return try {
            database.collection(Item.COLLECTION_PATH).document(itemId).delete().await()
            InternetResult.Success(null)
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

}