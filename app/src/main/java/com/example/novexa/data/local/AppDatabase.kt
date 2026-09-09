package com.example.novexa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.novexa.data.local.converter.Converters
import com.example.novexa.data.local.dao.*
import com.example.novexa.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * AppDatabase is the central Room database for Novexa.
 * Contains the Product entity, DAOs, and type converters for date/time and complex objects.
 * Configured for offline caching and reactive Flow updates.
 */
@Database(
    entities = [
        ProductEntity::class,
        ProductVariantEntity::class,
        CategoryEntity::class,
        UserEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        InventoryTransactionEntity::class,
        CouponEntity::class,
        ReviewEntity::class,
        AddressEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productVariantDao(): ProductVariantDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun couponDao(): CouponDao
    abstract fun reviewDao(): ReviewDao
    abstract fun addressDao(): AddressDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val DATABASE_NAME = "novexa_ecommerce_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return getDatabase(context, CoroutineScope(Dispatchers.IO))
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    DatabaseSeedData.populateInitialData(database)
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    try {
                        if (database.productDao().getProductCount() == 0) {
                            DatabaseSeedData.populateInitialData(database)
                        }
                    } catch (e: Exception) {
                        // Ignore if database is still opening
                    }
                }
            }
        }
    }
}
