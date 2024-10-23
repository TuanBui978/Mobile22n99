package com.example.myapplication.repository

import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Item

interface ItemRepository {
    suspend fun createItem(item: Item): InternetResult<Void>
    suspend fun getItemById(itemId: String): InternetResult<Item>
    suspend fun getItemsByShopId(shopId: String): InternetResult<List<Item>>
    suspend fun getAllItems(): InternetResult<List<Item>>
    suspend fun updateItem(item: Item): InternetResult<Void>
    suspend fun deleteItem(itemId: String): InternetResult<Void>
}