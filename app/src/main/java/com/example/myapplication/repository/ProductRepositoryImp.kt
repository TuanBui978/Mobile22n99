package com.example.myapplication.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.Item
import com.example.myapplication.model.Product
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await



    class ProductRepositoryImp : ProductRepository {
        private val database = Firebase.firestore
        private val storage = Firebase.storage

        override suspend fun createProduct(product: Product): InternetResult<Void> {
            val uploadedRefs = mutableListOf<StorageReference>()
            return try {
                val mainImage = product.mainImage!!.toUri()
                val mainRef = storage.reference.child("images/${mainImage.lastPathSegment}")
                mainRef.putFile(mainImage).await()
                uploadedRefs.add(mainRef)
                product.mainImage = mainRef.downloadUrl.await().toString()
                val images = product.images.map { uriString ->
                    val file = uriString.toUri()
                    val ref = storage.reference.child("images/${file.lastPathSegment}")
                    ref.putFile(file).await() // Đợi upload xong
                    uploadedRefs.add(ref) // Thêm vào danh sách đã upload thành công
                    ref.downloadUrl.await().toString() // Lấy URL của ảnh
                }.toList()

                product.images = images.toMutableList()
                val productRef = database.collection(Product.COLLECTION_PATH).document()
                product.id = productRef.id
                productRef.set(product).await()
                InternetResult.Success(null)
            } catch (e: Exception) {
                // Xóa tất cả ảnh đã upload nếu gặp lỗi
                uploadedRefs.forEach { ref ->
                    try {
                        ref.delete().await()
                    } catch (deleteException: Exception) {
                        // Log lỗi nếu xóa thất bại
                        Log.e(
                            "Delete Image Error",
                            "Failed to delete uploaded image: ${ref.path}",
                            deleteException
                        )
                    }
                }
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
            val uploadedRefs = mutableListOf<StorageReference>()
            return try {
                val mainImage = product.mainImage?.toUri()
                if (mainImage != null) {
                    val mainRef = storage.reference.child("images/${mainImage.lastPathSegment}")
                    try {
                        mainRef.putFile(mainImage).await()
                        uploadedRefs.add(mainRef)
                        product.mainImage = mainRef.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        Log.e("Upload Main Image", "Failed to upload main image: ${mainImage.lastPathSegment}", e)
                    }
                }

                val images: List<Deferred<String>> = product.images.map { uriString ->
                    val file = uriString.toUri()
                    val ref = storage.reference.child("images/${file.lastPathSegment}")
                    CoroutineScope(Dispatchers.IO).async {
                        try {
                            ref.putFile(file).await()
                            uploadedRefs.add(ref)
                            ref.downloadUrl.await().toString()
                        } catch (e: Exception) {
                            Log.e("Upload Image", "Failed to upload image: ${file.lastPathSegment}", e)
                            uriString // Nếu thất bại, giữ lại URL cũ
                        }
                    }
                }

                product.images = images.awaitAll().toMutableList()
                database.collection(Product.COLLECTION_PATH).document(product.id!!).set(product, SetOptions.merge()).await()
                InternetResult.Success(null)
            } catch (e: Exception) {
                // Xóa tất cả ảnh đã upload nếu gặp lỗi
                uploadedRefs.forEach { ref ->
                    try {
                        ref.delete().await()
                    } catch (deleteException: Exception) {
                        Log.e("Delete Image Error", "Failed to delete uploaded image: ${ref.path}", deleteException)
                    }
                }
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
