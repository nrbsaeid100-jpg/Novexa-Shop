package com.example.novexa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String = "CUSTOMER", // CUSTOMER, ADMIN, STAFF, SUPER_ADMIN
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val slug: String,
    val iconName: String,
    val parentId: Long? = null,
    val displayOrder: Int = 0
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String,
    val slug: String,
    val categoryId: Long,
    val subcategoryId: Long? = null,
    val brand: String,
    val description: String,
    val shortDescription: String,
    val price: Double,
    val compareAtPrice: Double,
    val costPrice: Double,
    val stockQuantity: Int,
    val lowStockThreshold: Int = 5,
    val isFeatured: Boolean = false,
    val isBestseller: Boolean = false,
    val isNewArrival: Boolean = false,
    val isFlashSale: Boolean = false,
    val weight: Double = 0.5,
    val dimensions: String = "10x10x5 cm",
    val tags: String = "", // Comma-separated tags
    val imageUrl: String,
    val galleryUrls: String = "", // Comma-separated image urls
    val rating: Double = 4.8,
    val reviewCount: Int = 12,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
) {
    val discountPercent: Int
        get() = if (compareAtPrice > price && compareAtPrice > 0) {
            (((compareAtPrice - price) / compareAtPrice) * 100).toInt()
        } else 0

    val stockStatus: String
        get() = when {
            stockQuantity <= 0 -> "Out of Stock"
            stockQuantity <= lowStockThreshold -> "Low Stock"
            else -> "In Stock"
        }
}

@Entity(tableName = "product_variants")
data class ProductVariantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val sku: String,
    val title: String, // e.g., "Midnight Blue / 8GB / 128GB"
    val price: Double,
    val stock: Int,
    val attributesJson: String // e.g. "Color: Blue, Size: L"
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0, // 0 for guest
    val productId: Long,
    val variantId: Long? = null,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String, // e.g. NVX-2026-000101
    val userId: Long,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val division: String,
    val district: String,
    val upazila: String,
    val fullAddress: String,
    val deliveryInstructions: String = "",
    val subtotal: Double,
    val discount: Double,
    val shippingFee: Double,
    val tax: Double = 0.0,
    val total: Double,
    val paymentMethod: String, // COD, bKash, Nagad, Rocket, Online
    val paymentStatus: String, // Pending, Paid, Failed, COD
    val orderStatus: String, // Pending, Confirmed, Processing, Shipped, Delivered, Cancelled, Returned, Refunded
    val paymentTransactionId: String = "",
    val customerNotes: String = "",
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val variantId: Long? = null,
    val productName: String,
    val variantTitle: String = "",
    val sku: String,
    val price: Double,
    val quantity: Int,
    val total: Double
)

@Entity(tableName = "inventory_transactions")
data class InventoryTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String, // PURCHASE, SALE, RETURN, MANUAL_ADJUSTMENT, DAMAGED, CANCELLED_ORDER
    val quantityChange: Int,
    val previousStock: Int,
    val newStock: Int,
    val referenceNote: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val type: String, // PERCENTAGE, FIXED, FREE_SHIPPING
    val value: Double,
    val minOrder: Double = 0.0,
    val maxDiscount: Double = 1000.0,
    val usageLimit: Int = 500,
    val usedCount: Int = 0,
    val isActive: Boolean = true,
    val expiryDateString: String = "2026-12-31"
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val userId: Long,
    val userName: String,
    val rating: Int, // 1 - 5
    val reviewText: String,
    val isVerifiedPurchase: Boolean = true,
    val helpfulCount: Int = 0,
    val isApproved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String = "Home", // Home, Office
    val fullName: String,
    val phone: String,
    val division: String,
    val district: String,
    val upazila: String,
    val fullAddress: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String, // ORDER_STATUS, PAYMENT, PROMOTION, INVENTORY
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Typealias for core Product domain entity
 */
typealias Product = ProductEntity
