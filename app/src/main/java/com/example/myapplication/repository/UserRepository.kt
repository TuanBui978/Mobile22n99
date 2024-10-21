package com.example.myapplication.repository

import android.content.Context
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User

interface UserRepository {
    suspend fun getCurrentUser(): InternetResult<User>
    suspend fun signIn(context: Context, email: String, password: String): InternetResult<User>
    suspend fun signUp(context: Context, email: String, password: String): InternetResult<User>
    fun signOut()
    suspend fun addUser(context: Context, user: User): InternetResult<Void>
    suspend fun removeUser(context: Context, uid: String): InternetResult<Void>
    suspend fun updateUser(context: Context, user: User): InternetResult<Void>
    suspend fun getUser(context: Context, uid: String): InternetResult<User>
}