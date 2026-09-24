package com.example.novexa.di

import android.content.Context
import androidx.room.Room
import com.example.novexa.data.local.AppDatabase
import com.example.novexa.data.local.dao.*
import com.example.novexa.data.repository.NovexaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Dependency Injection Module for Database and DAO providers.
 * Configures the Room database builder, offline-first Room instance, and DAOs.
 */
object DatabaseModule {

    private const val DB_NAME = AppDatabase.DATABASE_NAME

    @Volatile
    private var databaseInstance: AppDatabase? = null

    /**
     * Configures and returns the RoomDatabase.Builder for [AppDatabase].
     */
    fun createDatabaseBuilder(
        context: Context,
        databaseName: String = DB_NAME
    ): androidx.room.RoomDatabase.Builder<AppDatabase> {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            databaseName
        )
            .fallbackToDestructiveMigration()
            .fallbackToDestructiveMigrationOnDowngrade()
    }

    /**
     * Provides the singleton instance of [AppDatabase].
     */
    fun provideAppDatabase(
        context: Context,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): AppDatabase {
        return databaseInstance ?: synchronized(this) {
            databaseInstance ?: AppDatabase.getDatabase(context, scope).also {
                databaseInstance = it
            }
        }
    }

    /**
     * Provides the [ProductDao] instance from [AppDatabase] for caching and queries.
     */
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    /**
     * Overload providing [ProductDao] directly using context.
     */
    fun provideProductDao(context: Context): ProductDao {
        return provideAppDatabase(context).productDao()
    }

    // Additional DAO providers for complete data layer dependency injection
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()
    fun provideProductVariantDao(database: AppDatabase): ProductVariantDao = database.productVariantDao()
    fun provideCartDao(database: AppDatabase): CartDao = database.cartDao()
    fun provideWishlistDao(database: AppDatabase): WishlistDao = database.wishlistDao()
    fun provideOrderDao(database: AppDatabase): OrderDao = database.orderDao()
    fun provideOrderItemDao(database: AppDatabase): OrderItemDao = database.orderItemDao()
    fun provideInventoryDao(database: AppDatabase): InventoryDao = database.inventoryDao()
    fun provideCouponDao(database: AppDatabase): CouponDao = database.couponDao()
    fun provideReviewDao(database: AppDatabase): ReviewDao = database.reviewDao()
    fun provideAddressDao(database: AppDatabase): AddressDao = database.addressDao()
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()

    /**
     * Provides the [com.example.novexa.data.remote.firestore.FirestoreService] for Cloud Firestore synchronization.
     */
    fun provideFirestoreService(): com.example.novexa.data.remote.firestore.FirestoreService {
        return com.example.novexa.data.remote.firestore.FirestoreService()
    }

    /**
     * Provides the unified [NovexaRepository] injected with [AppDatabase] and [FirestoreService].
     */
    fun provideRepository(
        context: Context,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): NovexaRepository {
        val db = provideAppDatabase(context, scope)
        val firestoreService = provideFirestoreService()
        return NovexaRepository(db, firestoreService = firestoreService)
    }
}

/**
 * AppContainer interface for manual Dependency Injection throughout the application.
 */
interface AppContainer {
    val database: AppDatabase
    val productDao: ProductDao
    val firestoreService: com.example.novexa.data.remote.firestore.FirestoreService
    val repository: NovexaRepository
}

/**
 * Default implementation of [AppContainer] using [DatabaseModule].
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    override val database: AppDatabase by lazy {
        DatabaseModule.provideAppDatabase(context)
    }

    override val productDao: ProductDao by lazy {
        DatabaseModule.provideProductDao(database)
    }

    override val firestoreService: com.example.novexa.data.remote.firestore.FirestoreService by lazy {
        DatabaseModule.provideFirestoreService()
    }

    override val repository: NovexaRepository by lazy {
        DatabaseModule.provideRepository(context)
    }
}
