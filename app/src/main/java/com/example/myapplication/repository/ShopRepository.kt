package com.example.myapplication.repository

import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Shop

interface ShopRepository {
    suspend fun createShop(shop: Shop): InternetResult<Void>
    suspend fun getShopById(shopId: String): InternetResult<Shop>
    suspend fun getShopsByAccountId(accountId: String): InternetResult<List<Shop>>
    suspend fun updateShop(shop: Shop): InternetResult<Void>
    suspend fun deleteShop(shopId: String): InternetResult<Void>
}