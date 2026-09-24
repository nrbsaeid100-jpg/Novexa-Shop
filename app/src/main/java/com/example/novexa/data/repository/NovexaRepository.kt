package com.example.novexa.data.repository

import com.example.novexa.data.local.NovexaDatabase
import com.example.novexa.data.local.entity.*
import com.example.novexa.data.remote.firestore.FirestoreService
import com.example.novexa.payment.PaymentGatewayService
import com.example.novexa.payment.PaymentRequest
import com.example.novexa.payment.PaymentResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity,
    val variant: ProductVariantEntity? = null
)

data class CartPricingResult(
    val subtotal: Double,
    val discount: Double,
    val shippingFee: Double,
    val total: Double,
    val couponMessage: String? = null,
    val isCouponApplied: Boolean = false,
    val outOfStockItems: List<String> = emptyList()
)

data class CheckoutRequest(
    val userId: Long,
    val fullName: String,
    val phone: String,
    val email: String,
    val division: String,
    val district: String,
    val upazila: String,
    val fullAddress: String,
    val deliveryInstructions: String,
    val paymentMethod: String,
    val paymentPinOrCode: String,
    val couponCode: String?,
    val isInsideCity: Boolean
)

data class AdminAnalyticsSummary(
    val totalSales: Double,
    val todaySales: Double,
    val totalOrders: Int,
    val pendingOrders: Int,
    val deliveredOrders: Int,
    val cancelledOrders: Int,
    val totalCustomers: Int,
    val totalProducts: Int,
    val lowStockCount: Int,
    val inventoryValuation: Double,
    val grossProfit: Double,
    val averageOrderValue: Double
)

class NovexaRepository(
    private val database: NovexaDatabase,
    private val paymentGatewayService: PaymentGatewayService = PaymentGatewayService(),
    val firestoreService: FirestoreService = FirestoreService()
) {
    private val userDao = database.userDao()
    private val categoryDao = database.categoryDao()
    private val productDao = database.productDao()
    private val variantDao = database.productVariantDao()
    private val cartDao = database.cartDao()
    private val wishlistDao = database.wishlistDao()
    private val orderDao = database.orderDao()
    private val orderItemDao = database.orderItemDao()
    private val inventoryDao = database.inventoryDao()
    private val couponDao = database.couponDao()
    private val reviewDao = database.reviewDao()
    private val addressDao = database.addressDao()
    private val notificationDao = database.notificationDao()

    // --- Authentication ---
    suspend fun login(email: String, passwordHash: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim().lowercase())
        if (user == null) {
            Result.failure(Exception("No account found with this email address"))
        } else if (user.passwordHash != passwordHash) {
            Result.failure(Exception("Incorrect password entered"))
        } else {
            Result.success(user)
        }
    }

    suspend fun register(name: String, email: String, phone: String, passwordHash: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(normalizedEmail)
        if (existing != null) {
            Result.failure(Exception("Account already exists with this email"))
        } else {
            val newUser = UserEntity(
                name = name.trim(),
                email = normalizedEmail,
                phone = phone.trim(),
                passwordHash = passwordHash,
                role = "CUSTOMER"
            )
            val id = userDao.insertUser(newUser)
            Result.success(newUser.copy(id = id))
        }
    }

    suspend fun getUser(userId: Long): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    // --- Categories ---
    fun getCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    suspend fun saveCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.insert(category)
    }
    suspend fun deleteCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.delete(category)
    }

    // --- Products ---
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getFeaturedProducts(): Flow<List<ProductEntity>> = productDao.getFeaturedProducts()
    fun getFlashSaleProducts(): Flow<List<ProductEntity>> = productDao.getFlashSaleProducts()
    fun getBestsellers(): Flow<List<ProductEntity>> = productDao.getBestsellers()
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>> = productDao.getProductsByCategory(categoryId)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)
    fun getLowStockProducts(): Flow<List<ProductEntity>> = productDao.getLowStockProducts()

    suspend fun getProduct(id: Long): ProductEntity? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    fun getVariants(productId: Long): Flow<List<ProductVariantEntity>> = variantDao.getVariantsForProduct(productId)

    suspend fun saveProduct(product: ProductEntity, isNew: Boolean = false): Long = withContext(Dispatchers.IO) {
        val id = productDao.insertProduct(product)
        val savedProduct = product.copy(id = id)
        if (isNew) {
            inventoryDao.insertTransaction(
                InventoryTransactionEntity(
                    productId = id,
                    productName = product.name,
                    type = "PURCHASE",
                    quantityChange = product.stockQuantity,
                    previousStock = 0,
                    newStock = product.stockQuantity,
                    referenceNote = "New product created in catalog"
                )
            )
        }
        // Cloud Firestore Sync for Product Catalog
        firestoreService.saveProduct(savedProduct)
        id
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
        firestoreService.deleteProduct(product)
    }

    // --- Offline-First Caching & Data Synchronization ---
    suspend fun cacheProducts(products: List<ProductEntity>) = withContext(Dispatchers.IO) {
        productDao.insertAll(products)
    }

    suspend fun syncProductCatalog(remoteProducts: List<ProductEntity>) = withContext(Dispatchers.IO) {
        productDao.insertAll(remoteProducts.map { it.copy(isSynced = true, updatedAt = System.currentTimeMillis()) })
        firestoreService.syncProductCatalog(remoteProducts)
    }

    /**
     * Pull remote product updates from Cloud Firestore into local Room cache
     */
    suspend fun refreshCatalogFromFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        val result = firestoreService.fetchProducts()
        result.fold(
            onSuccess = { remoteProducts ->
                if (remoteProducts.isNotEmpty()) {
                    productDao.insertAll(remoteProducts.map { it.copy(isSynced = true) })
                }
                Result.success(remoteProducts.size)
            },
            onFailure = { Result.failure(it) }
        )
    }

    /**
     * Push entire local catalog to Firestore to initialize the cloud database
     */
    suspend fun pushLocalCatalogToFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        val localProducts = productDao.getAllProducts().first()
        firestoreService.syncProductCatalog(localProducts)
    }

    suspend fun getProductCount(): Int = withContext(Dispatchers.IO) {
        productDao.getProductCount()
    }

    suspend fun clearProductCache() = withContext(Dispatchers.IO) {
        productDao.deleteAllProducts()
    }

    // --- Cart ---
    fun getCartWithProducts(userId: Long): Flow<List<CartItemWithProduct>> {
        return combine(
            cartDao.getCartItems(userId),
            productDao.getAllProducts()
        ) { cartItems, allProducts ->
            val productMap = allProducts.associateBy { it.id }
            cartItems.mapNotNull { item ->
                val prod = productMap[item.productId]
                if (prod != null) {
                    CartItemWithProduct(cartItem = item, product = prod)
                } else null
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addToCart(userId: Long, productId: Long, variantId: Long? = null, quantity: Int = 1): Result<Unit> = withContext(Dispatchers.IO) {
        val product = productDao.getProductById(productId) ?: return@withContext Result.failure(Exception("Product not found"))
        if (product.stockQuantity < quantity) {
            return@withContext Result.failure(Exception("Only ${product.stockQuantity} items in stock"))
        }
        val existing = cartDao.findCartItem(userId, productId, variantId)
        val cartItemToSync: CartItemEntity
        if (existing != null) {
            val newQty = existing.quantity + quantity
            if (newQty > product.stockQuantity) {
                return@withContext Result.failure(Exception("Cannot add more than available stock (${product.stockQuantity})"))
            }
            cartDao.updateQuantity(existing.id, newQty)
            cartItemToSync = existing.copy(quantity = newQty)
        } else {
            val newItem = CartItemEntity(
                userId = userId,
                productId = productId,
                variantId = variantId,
                quantity = quantity
            )
            val insertedId = cartDao.insertCartItem(newItem)
            cartItemToSync = newItem.copy(id = insertedId)
        }
        // Cloud Firestore Sync for Shopping Cart
        firestoreService.saveCartItem(cartItemToSync)
        Result.success(Unit)
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int, maxStock: Int): Boolean = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(cartItemId)
            true
        } else if (quantity <= maxStock) {
            cartDao.updateQuantity(cartItemId, quantity)
            true
        } else {
            false
        }
    }

    suspend fun removeFromCart(cartItemId: Long) = withContext(Dispatchers.IO) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart(userId: Long) = withContext(Dispatchers.IO) {
        cartDao.clearCart(userId)
        // Cloud Firestore Sync: Clear User Cart
        firestoreService.clearUserCart(userId)
    }

    // --- Server-side Price & Stock Calculation ---
    suspend fun calculateCartTotals(
        items: List<CartItemWithProduct>,
        couponCode: String?,
        isInsideCity: Boolean
    ): CartPricingResult = withContext(Dispatchers.IO) {
        val outOfStock = mutableListOf<String>()
        var subtotal = 0.0

        for (item in items) {
            val dbProduct = productDao.getProductById(item.product.id)
            if (dbProduct == null || dbProduct.stockQuantity < item.cartItem.quantity) {
                outOfStock.add(item.product.name)
            }
            // Strict server-side price check from database
            val actualPrice = dbProduct?.price ?: item.product.price
            subtotal += actualPrice * item.cartItem.quantity
        }

        var shippingFee = if (isInsideCity) 60.0 else 120.0
        var discount = 0.0
        var couponMsg: String? = null
        var isCouponValid = false

        if (!couponCode.isNullOrBlank()) {
            val coupon = couponDao.getCouponByCode(couponCode.trim().uppercase())
            if (coupon != null && coupon.isActive) {
                if (subtotal >= coupon.minOrder) {
                    when (coupon.type) {
                        "PERCENTAGE" -> {
                            discount = (subtotal * (coupon.value / 100.0)).coerceAtMost(coupon.maxDiscount)
                            couponMsg = "Coupon applied: ${coupon.value}% discount (-৳${discount.toInt()})"
                            isCouponValid = true
                        }
                        "FIXED" -> {
                            discount = coupon.value.coerceAtMost(subtotal)
                            couponMsg = "Coupon applied: ৳${coupon.value.toInt()} discount"
                            isCouponValid = true
                        }
                        "FREE_SHIPPING" -> {
                            discount = shippingFee
                            couponMsg = "Coupon applied: Free shipping"
                            isCouponValid = true
                        }
                    }
                } else {
                    couponMsg = "Minimum order of ৳${coupon.minOrder.toInt()} required for this coupon"
                }
            } else {
                couponMsg = "Invalid or expired coupon code"
            }
        }

        val total = (subtotal - discount + shippingFee).coerceAtLeast(0.0)
        CartPricingResult(
            subtotal = subtotal,
            discount = discount,
            shippingFee = shippingFee,
            total = total,
            couponMessage = couponMsg,
            isCouponApplied = isCouponValid,
            outOfStockItems = outOfStock
        )
    }

    // --- Checkout & Order Placement ---
    suspend fun placeOrder(request: CheckoutRequest): Result<OrderEntity> = withContext(Dispatchers.IO) {
        val cartItems = cartDao.getCartItems(request.userId).first()
        if (cartItems.isEmpty()) {
            return@withContext Result.failure(Exception("Cart is empty"))
        }

        // 1. Verify stock & prices directly from database
        var calculatedSubtotal = 0.0
        val orderItemsToInsert = mutableListOf<OrderItemEntity>()

        for (ci in cartItems) {
            val product = productDao.getProductById(ci.productId)
                ?: return@withContext Result.failure(Exception("Product ID ${ci.productId} does not exist"))

            if (product.stockQuantity < ci.quantity) {
                return@withContext Result.failure(Exception("Not enough stock for ${product.name}. Available: ${product.stockQuantity}"))
            }

            val itemTotal = product.price * ci.quantity
            calculatedSubtotal += itemTotal

            orderItemsToInsert.add(
                OrderItemEntity(
                    orderId = 0, // Assigned after order insert
                    productId = product.id,
                    variantId = ci.variantId,
                    productName = product.name,
                    sku = product.sku,
                    price = product.price,
                    quantity = ci.quantity,
                    total = itemTotal
                )
            )
        }

        // 2. Validate Coupon & Shipping server-side
        var shippingFee = if (request.isInsideCity) 60.0 else 120.0
        var discount = 0.0

        if (!request.couponCode.isNullOrBlank()) {
            val coupon = couponDao.getCouponByCode(request.couponCode.trim().uppercase())
            if (coupon != null && coupon.isActive && calculatedSubtotal >= coupon.minOrder) {
                when (coupon.type) {
                    "PERCENTAGE" -> discount = (calculatedSubtotal * (coupon.value / 100.0)).coerceAtMost(coupon.maxDiscount)
                    "FIXED" -> discount = coupon.value.coerceAtMost(calculatedSubtotal)
                    "FREE_SHIPPING" -> discount = shippingFee
                }
            }
        }

        val finalTotal = (calculatedSubtotal - discount + shippingFee).coerceAtLeast(0.0)

        // 3. Generate Unique Order Number: NVX-2026-XXXXXX
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val randomSeq = (100000..999999).random()
        val orderNumber = "NVX-$currentYear-$randomSeq"

        // 4. Process Payment via Gateway Abstraction
        val paymentProvider = paymentGatewayService.getProvider(request.paymentMethod)
        val paymentRequest = PaymentRequest(
            orderNumber = orderNumber,
            amount = finalTotal,
            customerPhone = request.phone,
            paymentMethod = request.paymentMethod
        )

        val sessionId = paymentProvider.initiatePayment(paymentRequest)
        val paymentResult = paymentProvider.executePayment(sessionId, request.paymentPinOrCode, paymentRequest)

        val (paymentStatus, trxId) = when (paymentResult) {
            is PaymentResult.Success -> Pair(if (request.paymentMethod == "COD") "COD" else "Paid", paymentResult.transactionId)
            is PaymentResult.Failed -> return@withContext Result.failure(Exception("Payment failed: ${paymentResult.errorMessage}"))
            is PaymentResult.Cancelled -> return@withContext Result.failure(Exception("Payment was cancelled: ${paymentResult.reason}"))
        }

        // 5. Insert Order
        val order = OrderEntity(
            orderNumber = orderNumber,
            userId = request.userId,
            customerName = request.fullName,
            customerPhone = request.phone,
            customerEmail = request.email,
            division = request.division,
            district = request.district,
            upazila = request.upazila,
            fullAddress = request.fullAddress,
            deliveryInstructions = request.deliveryInstructions,
            subtotal = calculatedSubtotal,
            discount = discount,
            shippingFee = shippingFee,
            total = finalTotal,
            paymentMethod = request.paymentMethod,
            paymentStatus = paymentStatus,
            orderStatus = "Confirmed",
            paymentTransactionId = trxId
        )
        val orderId = orderDao.insertOrder(order)

        // 6. Insert Order Items & Deduct Stock & Record Inventory Transaction
        for (oi in orderItemsToInsert) {
            orderItemDao.insertAll(listOf(oi.copy(orderId = orderId)))

            val product = productDao.getProductById(oi.productId)!!
            val newStock = product.stockQuantity - oi.quantity
            productDao.updateStock(product.id, newStock)

            inventoryDao.insertTransaction(
                InventoryTransactionEntity(
                    productId = product.id,
                    productName = product.name,
                    type = "SALE",
                    quantityChange = -oi.quantity,
                    previousStock = product.stockQuantity,
                    newStock = newStock,
                    referenceNote = "Order $orderNumber sales fulfillment"
                )
            )
        }

        // 7. Clear Cart
        cartDao.clearCart(request.userId)
        firestoreService.clearUserCart(request.userId)

        // 8. Notification
        notificationDao.insertNotification(
            NotificationEntity(
                userId = request.userId,
                title = "Order Confirmed: $orderNumber",
                message = "Your order of ৳${finalTotal.toInt()} has been confirmed. Track status anytime in My Orders.",
                type = "ORDER_STATUS"
            )
        )

        val insertedOrder = order.copy(id = orderId)
        // Cloud Firestore Sync for Orders & Order Items
        firestoreService.saveOrder(insertedOrder, orderItemsToInsert)

        Result.success(insertedOrder)
    }

    // --- Order Operations ---
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    fun getOrdersForUser(userId: Long): Flow<List<OrderEntity>> = orderDao.getOrdersForUser(userId)
    suspend fun getOrderById(orderId: Long): OrderEntity? = withContext(Dispatchers.IO) { orderDao.getOrderById(orderId) }
    suspend fun getOrderByNumber(orderNum: String): OrderEntity? = withContext(Dispatchers.IO) { orderDao.getOrderByNumber(orderNum) }
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity> = withContext(Dispatchers.IO) { orderItemDao.getItemsForOrder(orderId) }
    fun getOrderItemsFlow(orderId: Long): Flow<List<OrderItemEntity>> = orderItemDao.getItemsForOrderFlow(orderId)

    suspend fun updateOrderStatus(orderId: Long, newStatus: String): Boolean = withContext(Dispatchers.IO) {
        val order = orderDao.getOrderById(orderId) ?: return@withContext false
        orderDao.updateOrderStatus(orderId, newStatus)

        // Cloud Firestore Sync for Order Status
        firestoreService.updateOrderStatus(order.orderNumber, order.userId, newStatus)

        // Restock if cancelled
        if (newStatus == "Cancelled" && order.orderStatus != "Cancelled") {
            val items = orderItemDao.getItemsForOrder(orderId)
            for (item in items) {
                val p = productDao.getProductById(item.productId)
                if (p != null) {
                    val newStock = p.stockQuantity + item.quantity
                    productDao.updateStock(p.id, newStock)
                    inventoryDao.insertTransaction(
                        InventoryTransactionEntity(
                            productId = p.id,
                            productName = p.name,
                            type = "CANCELLED_ORDER",
                            quantityChange = item.quantity,
                            previousStock = p.stockQuantity,
                            newStock = newStock,
                            referenceNote = "Restocked from Cancelled Order ${order.orderNumber}"
                        )
                    )
                }
            }
        }

        notificationDao.insertNotification(
            NotificationEntity(
                userId = order.userId,
                title = "Order ${order.orderNumber} is now $newStatus",
                message = "Status updated to $newStatus at ${SimpleDateFormat("hh:mm a, dd MMM", Locale.US).format(Date())}",
                type = "ORDER_STATUS"
            )
        )
        true
    }

    suspend fun cancelOrder(orderId: Long, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        val order = orderDao.getOrderById(orderId) ?: return@withContext Result.failure(Exception("Order not found"))
        if (order.orderStatus == "Delivered" || order.orderStatus == "Shipped") {
            return@withContext Result.failure(Exception("Cannot cancel order that has already been shipped or delivered"))
        }
        updateOrderStatus(orderId, "Cancelled")
        Result.success(Unit)
    }

    // --- Inventory & Stock ---
    fun getAllInventoryTransactions(): Flow<List<InventoryTransactionEntity>> = inventoryDao.getAllTransactions()

    suspend fun adjustStock(productId: Long, quantityChange: Int, type: String, note: String): Result<Unit> = withContext(Dispatchers.IO) {
        val product = productDao.getProductById(productId) ?: return@withContext Result.failure(Exception("Product not found"))
        val newStock = (product.stockQuantity + quantityChange).coerceAtLeast(0)
        productDao.updateStock(productId, newStock)
        inventoryDao.insertTransaction(
            InventoryTransactionEntity(
                productId = productId,
                productName = product.name,
                type = type,
                quantityChange = quantityChange,
                previousStock = product.stockQuantity,
                newStock = newStock,
                referenceNote = note
            )
        )
        Result.success(Unit)
    }

    // --- Wishlist ---
    fun getWishlist(userId: Long): Flow<List<ProductEntity>> {
        return combine(
            wishlistDao.getWishlistItems(userId),
            productDao.getAllProducts()
        ) { wishlist, allProducts ->
            val productMap = allProducts.associateBy { it.id }
            wishlist.mapNotNull { productMap[it.productId] }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun toggleWishlist(userId: Long, productId: Long): Boolean = withContext(Dispatchers.IO) {
        val count = wishlistDao.isInWishlist(userId, productId)
        if (count > 0) {
            wishlistDao.removeFromWishlist(userId, productId)
            false
        } else {
            wishlistDao.addToWishlist(WishlistItemEntity(userId = userId, productId = productId))
            true
        }
    }

    // --- Reviews ---
    fun getProductReviews(productId: Long): Flow<List<ReviewEntity>> = reviewDao.getReviewsForProduct(productId)
    fun getAllReviews(): Flow<List<ReviewEntity>> = reviewDao.getAllReviews()

    suspend fun submitReview(productId: Long, userId: Long, userName: String, rating: Int, comment: String): Result<Unit> = withContext(Dispatchers.IO) {
        reviewDao.insertReview(
            ReviewEntity(
                productId = productId,
                userId = userId,
                userName = userName,
                rating = rating,
                reviewText = comment
            )
        )
        Result.success(Unit)
    }

    // --- Coupons ---
    fun getAllCoupons(): Flow<List<CouponEntity>> = couponDao.getAllCoupons()
    suspend fun saveCoupon(coupon: CouponEntity) = withContext(Dispatchers.IO) {
        couponDao.insertCoupon(coupon)
    }

    // --- Notifications ---
    fun getNotifications(userId: Long): Flow<List<NotificationEntity>> = notificationDao.getNotificationsForUser(userId)
    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    // --- Addresses ---
    fun getAddresses(userId: Long): Flow<List<AddressEntity>> = addressDao.getAddressesForUser(userId)
    suspend fun saveAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        addressDao.insertAddress(address)
    }
    suspend fun setDefaultAddress(userId: Long, addressId: Long) = withContext(Dispatchers.IO) {
        addressDao.setDefaultAddress(userId, addressId)
    }
    suspend fun deleteAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        addressDao.deleteAddress(address)
    }

    // --- Admin Analytics ---
    suspend fun getAnalyticsSummary(): AdminAnalyticsSummary = withContext(Dispatchers.IO) {
        val orders = orderDao.getAllOrders().first()
        val products = productDao.getAllProducts().first()
        val users = userDao.getAllUsers().first()

        val totalSales = orders.filter { it.orderStatus != "Cancelled" }.sumOf { it.total }
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        val todaySales = orders.filter { it.createdAt >= todayStart && it.orderStatus != "Cancelled" }.sumOf { it.total }

        val pending = orders.count { it.orderStatus == "Pending" || it.orderStatus == "Confirmed" }
        val delivered = orders.count { it.orderStatus == "Delivered" }
        val cancelled = orders.count { it.orderStatus == "Cancelled" }

        val lowStock = products.count { it.stockQuantity <= it.lowStockThreshold }
        val valuation = products.sumOf { it.costPrice * it.stockQuantity }
        val estRevenue = products.sumOf { it.price * it.stockQuantity }
        val grossProfit = (estRevenue - valuation).coerceAtLeast(0.0)
        val validOrdersCount = orders.count { it.orderStatus != "Cancelled" }
        val aov = if (validOrdersCount > 0) totalSales / validOrdersCount else 0.0

        AdminAnalyticsSummary(
            totalSales = totalSales,
            todaySales = todaySales,
            totalOrders = orders.size,
            pendingOrders = pending,
            deliveredOrders = delivered,
            cancelledOrders = cancelled,
            totalCustomers = users.count { it.role == "CUSTOMER" },
            totalProducts = products.size,
            lowStockCount = lowStock,
            inventoryValuation = valuation,
            grossProfit = grossProfit,
            averageOrderValue = aov
        )
    }
}
