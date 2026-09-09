package com.example.novexa.data.local

import android.content.Context
import androidx.room.RoomDatabase
import com.example.novexa.data.local.dao.ProductDao
import com.example.novexa.data.local.entity.Product
import com.example.novexa.data.local.entity.ProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * AppDatabase is the central Room database instance providing DAO access
 * for offline-first product data caching and synchronization.
 */
typealias AppDatabase = NovexaDatabase

/**
 * Singleton factory providing access to the Room AppDatabase instance.
 */
object AppDatabaseFactory {
    fun getInstance(context: Context): AppDatabase {
        return NovexaDatabase.getDatabase(context)
    }

    fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
        return NovexaDatabase.getDatabase(context, scope)
    }
}
