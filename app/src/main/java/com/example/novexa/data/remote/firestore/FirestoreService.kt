package com.example.novexa.data.remote.firestore

import android.content.Context
import android.util.Log
import com.example.novexa.data.local.entity.CartItemEntity
import com.example.novexa.data.local.entity.OrderEntity
import com.example.novexa.data.local.entity.OrderItemEntity
import com.example.novexa.data.local.entity.ProductEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Data transfer model for Firestore Product document
 */
data class FirestoreProduct(
    val id: Long = 0,
    val name: String = "",
    val sku: String = "",
    val slug: String = "",
    val categoryId: Long = 1,
    val subcategoryId: Long? = null,
    val brand: String = "",
    val description: String = "",
    val shortDescription: String = "",
    val price: Double = 0.0,
    val compareAtPrice: Double = 0.0,
    val costPrice: Double = 0.0,
    val stockQuantity: Int = 0,
    val lowStockThreshold: Int = 5,
    val isFeatured: Boolean = false,
    val isBestseller: Boolean = false,
    val isNewArrival: Boolean = false,
    val isFlashSale: Boolean = false,
    val weight: Double = 0.5,
    val dimensions: String = "10x10x5 cm",
    val tags: String = "",
    val imageUrl: String = "",
    val galleryUrls: String = "",
    val rating: Double = 4.8,
    val reviewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toEntity(): ProductEntity = ProductEntity(
        id = id,
        name = name,
        sku = sku,
        slug = slug,
        categoryId = categoryId,
        subcategoryId = subcategoryId,
        brand = brand,
        description = description,
        shortDescription = shortDescription,
        price = price,
        compareAtPrice = compareAtPrice,
        costPrice = costPrice,
        stockQuantity = stockQuantity,
        lowStockThreshold = lowStockThreshold,
        isFeatured = isFeatured,
        isBestseller = isBestseller,
        isNewArrival = isNewArrival,
        isFlashSale = isFlashSale,
        weight = weight,
        dimensions = dimensions,
        tags = tags,
        imageUrl = imageUrl,
        galleryUrls = galleryUrls,
        rating = rating,
        reviewCount = reviewCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = true
    )

    companion object {
        fun fromEntity(entity: ProductEntity): FirestoreProduct = FirestoreProduct(
            id = entity.id,
            name = entity.name,
            sku = entity.sku,
            slug = entity.slug,
            categoryId = entity.categoryId,
            subcategoryId = entity.subcategoryId,
            brand = entity.brand,
            description = entity.description,
            shortDescription = entity.shortDescription,
            price = entity.price,
            compareAtPrice = entity.compareAtPrice,
            costPrice = entity.costPrice,
            stockQuantity = entity.stockQuantity,
            lowStockThreshold = entity.lowStockThreshold,
            isFeatured = entity.isFeatured,
            isBestseller = entity.isBestseller,
            isNewArrival = entity.isNewArrival,
            isFlashSale = entity.isFlashSale,
            weight = entity.weight,
            dimensions = entity.dimensions,
            tags = entity.tags,
            imageUrl = entity.imageUrl,
            galleryUrls = entity.galleryUrls,
            rating = entity.rating,
            reviewCount = entity.reviewCount,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}

/**
 * Data transfer model for Firestore User Order document
 */
data class FirestoreOrder(
    val id: Long = 0,
    val orderNumber: String = "",
    val userId: Long = 0,
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val division: String = "",
    val district: String = "",
    val upazila: String = "",
    val fullAddress: String = "",
    val deliveryInstructions: String = "",
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val shippingFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "COD",
    val paymentStatus: String = "Pending",
    val orderStatus: String = "Pending",
    val paymentTransactionId: String = "",
    val customerNotes: String = "",
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val items: List<FirestoreOrderItem> = emptyList()
) {
    fun toEntity(): OrderEntity = OrderEntity(
        id = id,
        orderNumber = orderNumber,
        userId = userId,
        customerName = customerName,
        customerPhone = customerPhone,
        customerEmail = customerEmail,
        division = division,
        district = district,
        upazila = upazila,
        fullAddress = fullAddress,
        deliveryInstructions = deliveryInstructions,
        subtotal = subtotal,
        discount = discount,
        shippingFee = shippingFee,
        tax = tax,
        total = total,
        paymentMethod = paymentMethod,
        paymentStatus = paymentStatus,
        orderStatus = orderStatus,
        paymentTransactionId = paymentTransactionId,
        customerNotes = customerNotes,
        adminNotes = adminNotes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromEntity(entity: OrderEntity, items: List<OrderItemEntity> = emptyList()): FirestoreOrder = FirestoreOrder(
            id = entity.id,
            orderNumber = entity.orderNumber,
            userId = entity.userId,
            customerName = entity.customerName,
            customerPhone = entity.customerPhone,
            customerEmail = entity.customerEmail,
            division = entity.division,
            district = entity.district,
            upazila = entity.upazila,
            fullAddress = entity.fullAddress,
            deliveryInstructions = entity.deliveryInstructions,
            subtotal = entity.subtotal,
            discount = entity.discount,
            shippingFee = entity.shippingFee,
            tax = entity.tax,
            total = entity.total,
            paymentMethod = entity.paymentMethod,
            paymentStatus = entity.paymentStatus,
            orderStatus = entity.orderStatus,
            paymentTransactionId = entity.paymentTransactionId,
            customerNotes = entity.customerNotes,
            adminNotes = entity.adminNotes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            items = items.map { FirestoreOrderItem.fromEntity(it) }
        )
    }
}

/**
 * Data transfer model for items within a Firestore Order
 */
data class FirestoreOrderItem(
    val orderId: Long = 0,
    val productId: Long = 0,
    val variantId: Long? = null,
    val productName: String = "",
    val variantTitle: String = "",
    val sku: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val total: Double = 0.0
) {
    fun toEntity(): OrderItemEntity = OrderItemEntity(
        id = 0,
        orderId = orderId,
        productId = productId,
        variantId = variantId,
        productName = productName,
        variantTitle = variantTitle,
        sku = sku,
        price = price,
        quantity = quantity,
        total = total
    )

    companion object {
        fun fromEntity(entity: OrderItemEntity): FirestoreOrderItem = FirestoreOrderItem(
            orderId = entity.orderId,
            productId = entity.productId,
            variantId = entity.variantId,
            productName = entity.productName,
            variantTitle = entity.variantTitle,
            sku = entity.sku,
            price = entity.price,
            quantity = entity.quantity,
            total = entity.total
        )
    }
}

/**
 * Data transfer model for Firestore User Shopping Cart item
 */
data class FirestoreCartItem(
    val id: Long = 0,
    val userId: Long = 0,
    val productId: Long = 0,
    val variantId: Long? = null,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toEntity(): CartItemEntity = CartItemEntity(
        id = id,
        userId = userId,
        productId = productId,
        variantId = variantId,
        quantity = quantity,
        addedAt = addedAt
    )

    companion object {
        fun fromEntity(entity: CartItemEntity): FirestoreCartItem = FirestoreCartItem(
            id = entity.id,
            userId = entity.userId,
            productId = entity.productId,
            variantId = entity.variantId,
            quantity = entity.quantity,
            addedAt = entity.addedAt
        )
    }
}

/**
 * Service to manage Product Catalogs, User Orders, and Shopping Cart Data in Cloud Firestore.
 * Handles graceful degradation if Firebase is not yet initialized or offline.
 */
class FirestoreService(
    private val firestore: FirebaseFirestore? = null
) {
    private val TAG = "FirestoreService"

    private fun getDb(): FirebaseFirestore? {
        return try {
            firestore ?: if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not yet initialized or available: ${e.message}")
            null
        }
    }

    val isAvailable: Boolean
        get() = getDb() != null

    // -------------------------------------------------------------
    // Product Catalog Management
    // Collection: "products"
    // -------------------------------------------------------------

    /**
     * Upsert a product into the cloud catalog.
     */
    suspend fun saveProduct(product: ProductEntity): Result<Unit> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val docId = if (product.sku.isNotBlank()) product.sku else product.id.toString()
            val firestoreProduct = FirestoreProduct.fromEntity(product)
            db.collection(COLLECTION_PRODUCTS)
                .document(docId)
                .set(firestoreProduct, SetOptions.merge())
                .await()
            Log.d(TAG, "Product successfully synced to Firestore: $docId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving product to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Batch sync multiple products to Firestore.
     */
    suspend fun syncProductCatalog(products: List<ProductEntity>): Result<Int> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val batch = db.batch()
            var count = 0
            for (product in products) {
                val docId = if (product.sku.isNotBlank()) product.sku else product.id.toString()
                val docRef = db.collection(COLLECTION_PRODUCTS).document(docId)
                batch.set(docRef, FirestoreProduct.fromEntity(product), SetOptions.merge())
                count++
            }
            batch.commit().await()
            Result.success(count)
        } catch (e: Exception) {
            Log.e(TAG, "Error batch syncing product catalog to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Fetch all remote products from the catalog.
     */
    suspend fun fetchProducts(): Result<List<ProductEntity>> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val snapshot = db.collection(COLLECTION_PRODUCTS)
                .get()
                .await()
            val products = snapshot.documents.mapNotNull { it.toObject(FirestoreProduct::class.java)?.toEntity() }
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products from Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Real-time stream of product catalog updates from Firestore.
     */
    fun observeProducts(): Flow<List<ProductEntity>> = callbackFlow {
        val db = getDb()
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = db.collection(COLLECTION_PRODUCTS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for products", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull {
                        it.toObject(FirestoreProduct::class.java)?.toEntity()
                    }
                    trySend(products)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Delete a product from the Firestore catalog.
     */
    suspend fun deleteProduct(product: ProductEntity): Result<Unit> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val docId = if (product.sku.isNotBlank()) product.sku else product.id.toString()
            db.collection(COLLECTION_PRODUCTS).document(docId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting product $product from Firestore", e)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // User Shopping Cart Management
    // Collection: "users/{userId}/cart/{cartItemId}"
    // -------------------------------------------------------------

    /**
     * Sync or update a single cart item in Firestore.
     */
    suspend fun saveCartItem(cartItem: CartItemEntity): Result<Unit> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val userCartRef = db.collection(COLLECTION_USERS)
                .document(cartItem.userId.toString())
                .collection(COLLECTION_CART)

            val docId = if (cartItem.variantId != null) {
                "${cartItem.productId}_${cartItem.variantId}"
            } else {
                cartItem.productId.toString()
            }

            userCartRef.document(docId)
                .set(FirestoreCartItem.fromEntity(cartItem), SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving cart item to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Sync entire cart for a user.
     */
    suspend fun syncUserCart(userId: Long, items: List<CartItemEntity>): Result<Unit> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val userCartRef = db.collection(COLLECTION_USERS)
                .document(userId.toString())
                .collection(COLLECTION_CART)

            // Clear existing and set current
            val existingSnapshot = userCartRef.get().await()
            val batch = db.batch()
            for (doc in existingSnapshot.documents) {
                batch.delete(doc.reference)
            }
            for (item in items) {
                val docId = if (item.variantId != null) "${item.productId}_${item.variantId}" else item.productId.toString()
                batch.set(userCartRef.document(docId), FirestoreCartItem.fromEntity(item))
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing user cart to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Remove a cart item from Firestore.
     */
    suspend fun removeCartItem(userId: Long, productId: Long, variantId: Long? = null): Result<Unit> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val docId = if (variantId != null) "${productId}_${variantId}" else productId.toString()
            db.collection(COLLECTION_USERS)
                .document(userId.toString())
                .collection(COLLECTION_CART)
                .document(docId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing cart item from Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Clear all cart items for a user in Firestore.
     */
    suspend fun clearUserCart(userId: Long): Result<Unit> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val userCartRef = db.collection(COLLECTION_USERS)
                .document(userId.toString())
                .collection(COLLECTION_CART)
            val snapshot = userCartRef.get().await()
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing user cart in Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Observe a user's real-time cart items from Firestore.
     */
    fun observeUserCart(userId: Long): Flow<List<CartItemEntity>> = callbackFlow {
        val db = getDb()
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = db.collection(COLLECTION_USERS)
            .document(userId.toString())
            .collection(COLLECTION_CART)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for user cart", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val cartItems = snapshot.documents.mapNotNull {
                        it.toObject(FirestoreCartItem::class.java)?.toEntity()
                    }
                    trySend(cartItems)
                }
            }

        awaitClose { listener.remove() }
    }

    // -------------------------------------------------------------
    // User Orders Management
    // Collection: "orders/{orderNumber}"
    // -------------------------------------------------------------

    /**
     * Save an order and its items in Firestore.
     */
    suspend fun saveOrder(order: OrderEntity, items: List<OrderItemEntity>): Result<Unit> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val firestoreOrder = FirestoreOrder.fromEntity(order, items)
            val docId = order.orderNumber.ifBlank { order.id.toString() }

            // Save to top-level orders collection
            db.collection(COLLECTION_ORDERS)
                .document(docId)
                .set(firestoreOrder, SetOptions.merge())
                .await()

            // Also mirror under user's order sub-collection for fast user query
            db.collection(COLLECTION_USERS)
                .document(order.userId.toString())
                .collection(COLLECTION_ORDERS)
                .document(docId)
                .set(firestoreOrder, SetOptions.merge())
                .await()

            Log.d(TAG, "Order successfully saved to Firestore: $docId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving order to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Update order status in Firestore (e.g. from Admin console).
     */
    suspend fun updateOrderStatus(
        orderNumber: String,
        userId: Long,
        newStatus: String,
        paymentStatus: String? = null
    ): Result<Unit> {
        val db = getDb() ?: return Result.failure(Exception("Firestore service is not connected"))
        return try {
            val updates = mutableMapOf<String, Any>(
                "orderStatus" to newStatus,
                "updatedAt" to System.currentTimeMillis()
            )
            if (paymentStatus != null) {
                updates["paymentStatus"] = paymentStatus
            }

            db.collection(COLLECTION_ORDERS)
                .document(orderNumber)
                .update(updates)
                .await()

            if (userId > 0) {
                db.collection(COLLECTION_USERS)
                    .document(userId.toString())
                    .collection(COLLECTION_ORDERS)
                    .document(orderNumber)
                    .update(updates)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status in Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Observe orders for a specific user from Firestore.
     */
    fun observeUserOrders(userId: Long): Flow<List<OrderEntity>> = callbackFlow {
        val db = getDb()
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = db.collection(COLLECTION_USERS)
            .document(userId.toString())
            .collection(COLLECTION_ORDERS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for user orders", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull {
                        it.toObject(FirestoreOrder::class.java)?.toEntity()
                    }
                    trySend(orders)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Observe all orders in Firestore (for Admin view).
     */
    fun observeAllOrders(): Flow<List<OrderEntity>> = callbackFlow {
        val db = getDb()
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = db.collection(COLLECTION_ORDERS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for all orders", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull {
                        it.toObject(FirestoreOrder::class.java)?.toEntity()
                    }
                    trySend(orders)
                }
            }

        awaitClose { listener.remove() }
    }

    companion object {
        const val COLLECTION_PRODUCTS = "products"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_CART = "cart"
        const val COLLECTION_ORDERS = "orders"
    }
}
