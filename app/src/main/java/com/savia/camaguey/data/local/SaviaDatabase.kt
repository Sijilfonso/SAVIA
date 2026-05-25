package com.savia.camaguey.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.savia.camaguey.data.model.*
import com.savia.camaguey.data.local.dao.*

@Database(
    entities = [
        Store::class,
        Product::class,
        CartItem::class,
        UserLocation::class,
        InteractionLog::class,
        AdminAccount::class,
        VisitStats::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SaviaDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun userLocationDao(): UserLocationDao
    abstract fun interactionLogDao(): InteractionLogDao
    abstract fun adminAccountDao(): AdminAccountDao
    abstract fun visitStatsDao(): VisitStatsDao
}
