package com.example.myapplication.repository

import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Variant

interface ItemRepository {
    suspend fun createItem(variant: Variant): InternetResult<Void>
    suspend fun getItemById(itemId: String): InternetResult<Variant>
    suspend fun getItemsByShopId(shopId: String): InternetResult<List<Variant>>
    suspend fun getAllItems(): InternetResult<List<Variant>>
    suspend fun getItemWithLimit(limit: Long): InternetResult<List<Variant>>
    suspend fun updateItem(variant: Variant): InternetResult<Void>
    suspend fun deleteItem(itemId: String): InternetResult<Void>

}