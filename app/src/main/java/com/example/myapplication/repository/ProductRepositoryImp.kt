package com.example.myapplication.repository

import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Item
import com.example.myapplication.model.Product
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await



    class ProductRepositoryImp : ProductRepository {
        private val database = Firebase.firestore

        override suspend fun createProduct(product: Product): InternetResult<Void> {
            return try {
                val productRef = database.collection(Product.COLLECTION_PATH).document()
                product.id = productRef.id
                productRef.set(product).await()
                InternetResult.Success(null)
            } catch (e: Exception) {
                InternetResult.Failed(e)
            }
        }

        override suspend fun getProductById(productId: String): InternetResult<Product> {
            return try {
                val productDoc = database.collection(Product.COLLECTION_PATH).document(productId).get().await()
                val product = productDoc.toObject(Product::class.java)
                if (product != null) {
                    InternetResult.Success(product)
                } else {
                    InternetResult.Failed(Exception("Product not found"))
                }
            } catch (e: Exception) {
                InternetResult.Failed(e)
            }
        }

        override suspend fun getProductWithLimit(limit: Long): InternetResult<List<Product>> {
            return try {
                val result = database.collection(Product.COLLECTION_PATH).limit(limit).get().await()
                val products = result.toObjects(Product::class.java)
                InternetResult.Success(products)
            } catch (e: Exception) {
                InternetResult.Failed(e)
            }
        }

        override suspend fun getAllProducts(): InternetResult<List<Product>> {
            return try {
                val result = database.collection(Product.COLLECTION_PATH).get().await()
                val products = result.toObjects(Product::class.java)
                InternetResult.Success(products)
            } catch (e: Exception) {
                InternetResult.Failed(e)
            }
        }


        override suspend fun updateProduct(product: Product): InternetResult<Void> {
            return try {
                database.collection(Product.COLLECTION_PATH).document(product.id!!).set(product, SetOptions.merge()).await()
                InternetResult.Success(null)
            } catch (e: Exception) {
                InternetResult.Failed(e)
            }
        }

        override suspend fun deleteProduct(productId: String): InternetResult<Void> {
            return try {
                database.collection(Product.COLLECTION_PATH).document(productId).delete().await()
                InternetResult.Success(null)
            } catch (e: Exception) {
                InternetResult.Failed(e)
            }
        }


        companion object {
            private var productRepository: ProductRepositoryImp? = null
            fun getInstance(): ProductRepositoryImp {
                if (productRepository == null) {
                    productRepository = ProductRepositoryImp()
                }
                return productRepository!!
            }
        }
    }
