package com.example.novexa.data.local

import com.example.novexa.data.local.entity.*

/**
 * Seed data utility for populating demo products, categories, coupons,
 * and accounts when the Room database is initialized or recreated.
 */
object DatabaseSeedData {

    suspend fun populateInitialData(db: AppDatabase) {
        // 1. Seed Users
        val adminUser = UserEntity(
            name = "Novexa Admin",
            email = "admin@novexa.com.bd",
            phone = "+8801811223344",
            passwordHash = "admin123",
            role = "SUPER_ADMIN"
        )
        val customerUser = UserEntity(
            name = "Rafiqul Islam",
            email = "customer@novexa.com.bd",
            phone = "+8801712345678",
            passwordHash = "user123",
            role = "CUSTOMER"
        )
        db.userDao().insertUser(adminUser)
        val customerId = db.userDao().insertUser(customerUser)

        // 2. Seed Categories
        val categories = listOf(
            CategoryEntity(id = 1, name = "Electronics", slug = "electronics", iconName = "Devices", displayOrder = 1),
            CategoryEntity(id = 2, name = "Fashion & Apparel", slug = "fashion", iconName = "Checkroom", displayOrder = 2),
            CategoryEntity(id = 3, name = "Cosmetics & Beauty", slug = "beauty", iconName = "Spa", displayOrder = 3),
            CategoryEntity(id = 4, name = "Home & Kitchen", slug = "home-kitchen", iconName = "Kitchen", displayOrder = 4),
            CategoryEntity(id = 5, name = "Grocery & Essentials", slug = "grocery", iconName = "ShoppingBasket", displayOrder = 5),
            CategoryEntity(id = 6, name = "Accessories & Gifts", slug = "accessories", iconName = "CardGiftcard", displayOrder = 6)
        )
        db.categoryDao().insertAll(categories)

        // 3. Seed Products
        val products = listOf(
            ProductEntity(
                id = 1,
                name = "Samsung Galaxy Buds2 Pro - Graphite",
                sku = "SAM-BUDS2P-GR",
                slug = "samsung-galaxy-buds2-pro",
                categoryId = 1,
                brand = "Samsung",
                description = "Experience studio quality sound with Active Noise Canceling, 24-bit Hi-Fi audio, and ergonomic comfort for prolonged listening.",
                shortDescription = "24-bit Hi-Fi audio with intelligent ANC and 360 Audio.",
                price = 14500.0,
                compareAtPrice = 18000.0,
                costPrice = 11200.0,
                stockQuantity = 24,
                lowStockThreshold = 5,
                isFeatured = true,
                isBestseller = true,
                isFlashSale = true,
                weight = 0.3,
                dimensions = "5x5x3 cm",
                tags = "audio,earbuds,wireless,samsung,noise-cancelling",
                imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80",
                rating = 4.9,
                reviewCount = 38
            ),
            ProductEntity(
                id = 2,
                name = "Anker PowerCore 20,000mAh PD Fast Bank",
                sku = "ANK-PC20K-PD",
                slug = "anker-powercore-20000",
                categoryId = 1,
                brand = "Anker",
                description = "Massive 20,000mAh capacity provides more than 5 charges for iPhone 15, almost 5 full charges for Samsung Galaxy S23, with 20W Power Delivery.",
                shortDescription = "High-speed 20W Power Delivery USB-C portable charger.",
                price = 3450.0,
                compareAtPrice = 4200.0,
                costPrice = 2600.0,
                stockQuantity = 45,
                lowStockThreshold = 8,
                isFeatured = true,
                isBestseller = true,
                isFlashSale = false,
                weight = 0.45,
                dimensions = "15x7x2.5 cm",
                tags = "anker,powerbank,charger,travel,usb-c",
                imageUrl = "https://images.unsplash.com/photo-1609592424109-dd9892f1b177?w=600&auto=format&fit=crop&q=80",
                rating = 4.8,
                reviewCount = 52
            ),
            ProductEntity(
                id = 3,
                name = "Handcrafted Dhaka Fine Cotton Panjabi",
                sku = "NVX-PANJ-01",
                slug = "dhaka-fine-cotton-panjabi",
                categoryId = 2,
                brand = "Novexa Heritage",
                description = "Exquisite premium 100% combed cotton Panjabi featuring delicate collar embroidery. Tailored for unmatched comfort and elegance across all seasons.",
                shortDescription = "Premium 100% fine cotton traditional men's Panjabi.",
                price = 2850.0,
                compareAtPrice = 3600.0,
                costPrice = 1750.0,
                stockQuantity = 18,
                lowStockThreshold = 6,
                isFeatured = true,
                isBestseller = false,
                isNewArrival = true,
                isFlashSale = true,
                weight = 0.4,
                dimensions = "30x25x2 cm",
                tags = "clothing,panjabi,eid,traditional,cotton",
                imageUrl = "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600&auto=format&fit=crop&q=80",
                rating = 4.9,
                reviewCount = 29
            ),
            ProductEntity(
                id = 4,
                name = "Pure Kashmiri Saffron Radiance Serum (30ml)",
                sku = "SKN-SAF-SRM",
                slug = "kashmiri-saffron-radiance-serum",
                categoryId = 3,
                brand = "Aura Organics",
                description = "Infused with genuine Grade-A saffron and Hyaluronic acid to brighten complexion, reduce dark spots, and hydrate dry skin deeply.",
                shortDescription = "Natural glow booster with Grade-A Kashmiri saffron.",
                price = 1250.0,
                compareAtPrice = 1650.0,
                costPrice = 650.0,
                stockQuantity = 32,
                lowStockThreshold = 10,
                isFeatured = false,
                isBestseller = true,
                isFlashSale = false,
                weight = 0.15,
                dimensions = "4x4x10 cm",
                tags = "skincare,serum,saffron,organic,beauty",
                imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600&auto=format&fit=crop&q=80",
                rating = 4.7,
                reviewCount = 44
            ),
            ProductEntity(
                id = 5,
                name = "Xiaomi Smart Air Fryer Pro 4L OLED",
                sku = "XIA-AF-4L",
                slug = "xiaomi-smart-air-fryer-pro",
                categoryId = 4,
                brand = "Xiaomi",
                description = "Cook healthy delicious meals with 360 degree heated air circulation, 40°C to 200°C low and high temperature cooking, and smart scheduling via app.",
                shortDescription = "4L transparent viewing window smart air fryer.",
                price = 8400.0,
                compareAtPrice = 10500.0,
                costPrice = 6700.0,
                stockQuantity = 12,
                lowStockThreshold = 4,
                isFeatured = true,
                isBestseller = false,
                isFlashSale = true,
                weight = 4.2,
                dimensions = "34x25x30 cm",
                tags = "kitchen,home,xiaomi,airfryer,appliance",
                imageUrl = "https://images.unsplash.com/photo-1585515320310-259814833e62?w=600&auto=format&fit=crop&q=80",
                rating = 4.8,
                reviewCount = 19
            ),
            ProductEntity(
                id = 6,
                name = "Sundarbans Wild Raw Floral Honey (500g)",
                sku = "GRO-SUN-HNY",
                slug = "sundarbans-wild-raw-honey",
                categoryId = 5,
                brand = "Green Delta",
                description = "100% pure raw unprocessed forest honey collected by traditional Mouwals from the heart of the Sundarbans mangrove biosphere.",
                shortDescription = "Authentic natural wild mangrove honey from Sundarbans.",
                price = 850.0,
                compareAtPrice = 990.0,
                costPrice = 520.0,
                stockQuantity = 60,
                lowStockThreshold = 10,
                isFeatured = false,
                isBestseller = true,
                isNewArrival = true,
                isFlashSale = false,
                weight = 0.7,
                dimensions = "8x8x14 cm",
                tags = "grocery,honey,organic,pure,sundarbans",
                imageUrl = "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&auto=format&fit=crop&q=80",
                rating = 5.0,
                reviewCount = 67
            ),
            ProductEntity(
                id = 7,
                name = "Novexa Commuter Water-Resistant Laptop Pack",
                sku = "BAG-NVX-WTR",
                slug = "novexa-commuter-laptop-backpack",
                categoryId = 6,
                brand = "Novexa Gear",
                description = "Premium weatherproof ballistic nylon backpack with 15.6 inch padded laptop compartment, anti-theft hidden pockets, and USB pass-through.",
                shortDescription = "Waterproof ballistic nylon laptop travel backpack.",
                price = 2190.0,
                compareAtPrice = 2890.0,
                costPrice = 1350.0,
                stockQuantity = 22,
                lowStockThreshold = 5,
                isFeatured = true,
                isBestseller = false,
                isFlashSale = false,
                weight = 0.85,
                dimensions = "45x30x16 cm",
                tags = "bag,backpack,laptop,travel,accessories",
                imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&auto=format&fit=crop&q=80",
                rating = 4.8,
                reviewCount = 31
            ),
            ProductEntity(
                id = 8,
                name = "Baseus 65W GaN5 Pro Multi-Port Fast Charger",
                sku = "BAS-GAN-65W",
                slug = "baseus-65w-gan5-pro-charger",
                categoryId = 1,
                brand = "Baseus",
                description = "Charge your laptop, tablet, and smartphone simultaneously with 2 USB-C ports and 1 USB-A port using latest Gallium Nitride 5th gen tech.",
                shortDescription = "Compact 3-port 65W GaN fast charger.",
                price = 2650.0,
                compareAtPrice = 3200.0,
                costPrice = 1900.0,
                stockQuantity = 3, // Low stock demo!
                lowStockThreshold = 5,
                isFeatured = false,
                isBestseller = false,
                isFlashSale = true,
                weight = 0.2,
                dimensions = "9x4x3 cm",
                tags = "charger,gan,baseus,usb-c,laptop",
                imageUrl = "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600&auto=format&fit=crop&q=80",
                rating = 4.9,
                reviewCount = 15
            )
        )
        db.productDao().insertAll(products)

        // 4. Seed Variants for Panjabi (Product 3)
        val variants = listOf(
            ProductVariantEntity(productId = 3, sku = "NVX-PANJ-M", title = "Size: M (Chest 40\")", price = 2850.0, stock = 6, attributesJson = "Size: M, Color: Classic White"),
            ProductVariantEntity(productId = 3, sku = "NVX-PANJ-L", title = "Size: L (Chest 42\")", price = 2850.0, stock = 7, attributesJson = "Size: L, Color: Classic White"),
            ProductVariantEntity(productId = 3, sku = "NVX-PANJ-XL", title = "Size: XL (Chest 44\")", price = 2950.0, stock = 5, attributesJson = "Size: XL, Color: Classic White")
        )
        db.productVariantDao().insertAll(variants)

        // 5. Seed Coupons
        val coupons = listOf(
            CouponEntity(
                code = "NOVEXA100",
                type = "FIXED",
                value = 100.0,
                minOrder = 1000.0,
                maxDiscount = 100.0,
                expiryDateString = "2026-12-31"
            ),
            CouponEntity(
                code = "EID15",
                type = "PERCENTAGE",
                value = 15.0,
                minOrder = 1500.0,
                maxDiscount = 500.0,
                expiryDateString = "2026-12-31"
            ),
            CouponEntity(
                code = "FREESHIP",
                type = "FREE_SHIPPING",
                value = 60.0,
                minOrder = 2000.0,
                maxDiscount = 120.0,
                expiryDateString = "2026-12-31"
            )
        )
        for (c in coupons) {
            db.couponDao().insertCoupon(c)
        }

        // 6. Seed Address for Customer
        val defaultAddress = AddressEntity(
            userId = customerId,
            title = "Home",
            fullName = "Rafiqul Islam",
            phone = "+8801712345678",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Gulshan-2",
            fullAddress = "House 24, Road 71, Block D, Gulshan-2, Dhaka 1212",
            isDefault = true
        )
        db.addressDao().insertAddress(defaultAddress)

        // 7. Seed Initial Inventory Transactions
        val transactions = listOf(
            InventoryTransactionEntity(
                productId = 1,
                productName = "Samsung Galaxy Buds2 Pro - Graphite",
                type = "PURCHASE",
                quantityChange = 24,
                previousStock = 0,
                newStock = 24,
                referenceNote = "Initial warehouse batch shipment #WH-101"
            ),
            InventoryTransactionEntity(
                productId = 8,
                productName = "Baseus 65W GaN5 Pro Multi-Port Fast Charger",
                type = "MANUAL_ADJUSTMENT",
                quantityChange = -2,
                previousStock = 5,
                newStock = 3,
                referenceNote = "Quality check verification adjustment"
            )
        )
        for (t in transactions) {
            db.inventoryDao().insertTransaction(t)
        }

        // 8. Seed Sample Reviews
        val reviews = listOf(
            ReviewEntity(
                productId = 1,
                userId = customerId,
                userName = "Zubair Ahmed",
                rating = 5,
                reviewText = "Sound quality is unbelievable! Genuine product and fast delivery inside Dhaka within 24 hours.",
                isVerifiedPurchase = true,
                helpfulCount = 8
            ),
            ReviewEntity(
                productId = 1,
                userId = customerId,
                userName = "Nabila Rahman",
                rating = 5,
                reviewText = "ANC works remarkably well in noisy traffic. Battery lasts all day.",
                isVerifiedPurchase = true,
                helpfulCount = 4
            ),
            ReviewEntity(
                productId = 3,
                userId = customerId,
                userName = "Kazi Mahfuz",
                rating = 5,
                reviewText = "Fabric is extremely comfortable and fitting was spot on. Very happy with Novexa service!",
                isVerifiedPurchase = true,
                helpfulCount = 6
            )
        )
        for (r in reviews) {
            db.reviewDao().insertReview(r)
        }

        // 9. Seed Sample Order for historical customer tracking
        val sampleOrder = OrderEntity(
            orderNumber = "NVX-2026-000101",
            userId = customerId,
            customerName = "Rafiqul Islam",
            customerPhone = "+8801712345678",
            customerEmail = "customer@novexa.com.bd",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Gulshan-2",
            fullAddress = "House 24, Road 71, Block D, Gulshan-2, Dhaka 1212",
            subtotal = 3450.0,
            discount = 100.0,
            shippingFee = 60.0,
            tax = 0.0,
            total = 3410.0,
            paymentMethod = "bKash",
            paymentStatus = "Paid",
            orderStatus = "Processing",
            paymentTransactionId = "BK-9X284M0",
            customerNotes = "Please call before delivery"
        )
        val orderId = db.orderDao().insertOrder(sampleOrder)
        val orderItem = OrderItemEntity(
            orderId = orderId,
            productId = 2,
            productName = "Anker PowerCore 20,000mAh PD Fast Bank",
            sku = "ANK-PC20K-PD",
            price = 3450.0,
            quantity = 1,
            total = 3450.0
        )
        db.orderItemDao().insertAll(listOf(orderItem))

        // 10. Seed Notification
        db.notificationDao().insertNotification(
            NotificationEntity(
                userId = customerId,
                title = "Welcome to Novexa!",
                message = "Welcome to Bangladesh's premium online multi-category store. Enjoy ৳100 off your first order with coupon NOVEXA100.",
                type = "PROMOTION"
            )
        )
    }
}
