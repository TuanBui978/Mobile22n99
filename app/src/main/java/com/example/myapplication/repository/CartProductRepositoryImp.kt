package com.example.myapplication.repository

import com.example.myapplication.model.CartProduct
import com.example.myapplication.model.InternetResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class CartProductRepositoryImp: CartProductRepository {
    private val database = Firebase.firestore

    // Thêm sản phẩm vào giỏ hàng
    override suspend fun addProductToCart(cartProduct: CartProduct): InternetResult<CartProduct> {
        return try {
            val cartProductRef = database.collection(CartProduct.DOCUMENT_PATH).document()
            cartProduct.id = cartProductRef.id // Gán ID tự động từ Firestore
            cartProductRef.set(cartProduct).await() // Lưu sản phẩm vào Firestore
            InternetResult.Success(cartProduct) // Trả về sản phẩm đã thêm
        } catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    // Lấy tất cả sản phẩm trong giỏ hàng của người dùng
    override suspend fun getAllProductsInCart(userId: String): InternetResult<List<CartProduct>> {
        return try {
            val result = database.collection(CartProduct.DOCUMENT_PATH)
                .whereEqualTo("uid", userId) // Giả sử bạn có trường userId để xác định giỏ hàng của người dùng
                .get().await()
            val cartProducts = result.toObjects(CartProduct::class.java)
            InternetResult.Success(cartProducts)
        } catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    override suspend fun removeProductFromCart(cartProductId: String): InternetResult<Void> {
        return try {
            val cartProductRef = database.collection(CartProduct.DOCUMENT_PATH).document(cartProductId)
            cartProductRef.delete().await() // Xóa sản phẩm khỏi Firestore
            InternetResult.Success(null)
        } catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun removeProductsFromCart(cartProductIds: List<String>): InternetResult<Void> {
        return try {
            // Thực hiện xóa từng sản phẩm khỏi Firestore
            cartProductIds.forEach { cartProductId ->
                val cartProductRef = database.collection(CartProduct.DOCUMENT_PATH).document(cartProductId)
                cartProductRef.delete().await() // Xóa sản phẩm
            }

            // Nếu tất cả đều thành công, trả về kết quả thành công
            InternetResult.Success(null)
        } catch (e: Exception) {
            // Xử lý lỗi nếu xảy ra
//            Log.e("CartRepository", "Lỗi khi xóa sản phẩm khỏi giỏ hàng", e)
            InternetResult.Failed(e)
        }
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    override suspend fun updateProductQuantity(cartProductId: String, quantity: Int): InternetResult<Void> {
        return try {
            val cartProductRef = database.collection(CartProduct.DOCUMENT_PATH).document(cartProductId)
            cartProductRef.update("count", quantity).await() // Cập nhật số lượng
            InternetResult.Success(null)
        } catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    // Lấy sản phẩm giỏ hàng của người dùng theo ID sản phẩm và ID người dùng
    override suspend fun getCartProductOfUserById(cartProductId: String, uid: String): InternetResult<CartProduct> {
        return try {
            val cartProductDoc = database.collection(CartProduct.DOCUMENT_PATH)
                .whereEqualTo("id", cartProductId)
                .whereEqualTo("uid", uid) // Giả sử bạn có trường userId để xác định giỏ hàng của người dùng
                .get().await()

            val cartProduct = cartProductDoc.documents.firstOrNull()?.toObject(CartProduct::class.java)
            if (cartProduct != null) {
                InternetResult.Success(cartProduct)
            } else {
                InternetResult.Failed(Exception("Cart product not found"))
            }
        } catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    companion object {
        private var cartProductRepository: CartProductRepositoryImp? = null

        // Singleton instance
        fun getInstance(): CartProductRepositoryImp {
            if (cartProductRepository == null) {
                cartProductRepository = CartProductRepositoryImp()
            }
            return cartProductRepository!!
        }
    }
}