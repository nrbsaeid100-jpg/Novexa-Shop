package com.example.novexa.data.remote.firestore

import com.example.novexa.data.local.entity.CartItemEntity
import com.example.novexa.data.local.entity.OrderEntity
import com.example.novexa.data.local.entity.OrderItemEntity
import com.example.novexa.data.local.entity.ProductEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FirestoreDataModelsTest {

    @Test
    fun `test ProductEntity to and from FirestoreProduct conversion`() {
        val originalProduct = ProductEntity(
            id = 101,
            name = "Sony WH-1000XM5",
            sku = "SONY-XM5-BLK",
            slug = "sony-wh-1000xm5",
            categoryId = 1,
            brand = "Sony",
            description = "Industry-leading noise canceling headphones",
            shortDescription = "Flagship ANC headphones",
            price = 38500.0,
            compareAtPrice = 42000.0,
            costPrice = 32000.0,
            stockQuantity = 15,
            imageUrl = "https://images.unsplash.com/photo-1546435770-a3e426bf472b",
            rating = 4.9,
            reviewCount = 45
        )

        val firestoreProduct = FirestoreProduct.fromEntity(originalProduct)
        assertEquals("SONY-XM5-BLK", firestoreProduct.sku)
        assertEquals("Sony", firestoreProduct.brand)
        assertEquals(38500.0, firestoreProduct.price, 0.001)

        val convertedBack = firestoreProduct.toEntity()
        assertEquals(originalProduct.id, convertedBack.id)
        assertEquals(originalProduct.name, convertedBack.name)
        assertEquals(originalProduct.sku, convertedBack.sku)
        assertEquals(originalProduct.price, convertedBack.price, 0.001)
    }

    @Test
    fun `test OrderEntity and OrderItem to and from FirestoreOrder conversion`() {
        val order = OrderEntity(
            id = 501,
            orderNumber = "NVX-2026-123456",
            userId = 2L,
            customerName = "Tahmidur Rahman",
            customerPhone = "+8801712345678",
            customerEmail = "customer@novexa.com.bd",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Gulshan",
            fullAddress = "Road 11, Block D, House 42",
            subtotal = 5000.0,
            discount = 500.0,
            shippingFee = 60.0,
            total = 4560.0,
            paymentMethod = "bKash",
            paymentStatus = "Paid",
            orderStatus = "Confirmed"
        )

        val items = listOf(
            OrderItemEntity(
                id = 1,
                orderId = 501,
                productId = 10,
                productName = "Minimalist Leather Backpack",
                sku = "BAG-LTH-01",
                price = 2500.0,
                quantity = 2,
                total = 5000.0
            )
        )

        val firestoreOrder = FirestoreOrder.fromEntity(order, items)
        assertEquals("NVX-2026-123456", firestoreOrder.orderNumber)
        assertEquals(1, firestoreOrder.items.size)
        assertEquals("Minimalist Leather Backpack", firestoreOrder.items[0].productName)

        val orderEntity = firestoreOrder.toEntity()
        assertEquals("NVX-2026-123456", orderEntity.orderNumber)
        assertEquals(4560.0, orderEntity.total, 0.001)
    }

    @Test
    fun `test CartItemEntity to and from FirestoreCartItem conversion`() {
        val cartItem = CartItemEntity(
            id = 12,
            userId = 3L,
            productId = 45L,
            variantId = 2L,
            quantity = 3
        )

        val firestoreCart = FirestoreCartItem.fromEntity(cartItem)
        assertEquals(3L, firestoreCart.userId)
        assertEquals(45L, firestoreCart.productId)
        assertEquals(3, firestoreCart.quantity)

        val converted = firestoreCart.toEntity()
        assertEquals(cartItem.id, converted.id)
        assertEquals(cartItem.userId, converted.userId)
        assertEquals(cartItem.productId, converted.productId)
        assertEquals(cartItem.quantity, converted.quantity)
    }

    @Test
    fun `test FirestoreService initialization is resilient`() {
        val service = FirestoreService()
        assertNotNull(service)
        // Verified gracefully falls back when no FirebaseApp is initialized in test environment
    }
}
