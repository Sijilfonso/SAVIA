package com.savia.camaguey.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.savia.camaguey.data.local.converter.StringListConverter
import com.savia.camaguey.data.local.dao.AdminAccountDao
import com.savia.camaguey.data.local.dao.CartDao
import com.savia.camaguey.data.local.dao.InteractionLogDao
import com.savia.camaguey.data.local.dao.PasswordResetDao
import com.savia.camaguey.data.local.dao.ProductDao
import com.savia.camaguey.data.local.dao.StoreDao
import com.savia.camaguey.data.local.dao.UserLocationDao
import com.savia.camaguey.data.local.dao.VisitStatsDao
import com.savia.camaguey.data.model.AdminAccount
import com.savia.camaguey.data.model.CartItem
import com.savia.camaguey.data.model.InteractionLog
import com.savia.camaguey.data.model.PasswordReset
import com.savia.camaguey.data.model.Product
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.data.model.UserLocation
import com.savia.camaguey.data.model.VisitStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Room Database para SAVIA.
 * 8 entidades, singleton, seed data en callback.
 */
@Database(
    entities = [
        Store::class,
        Product::class,
        CartItem::class,
        UserLocation::class,
        InteractionLog::class,
        AdminAccount::class,
        VisitStats::class,
        PasswordReset::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class SaviaDatabase : RoomDatabase() {

    abstract fun storeDao(): StoreDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun userLocationDao(): UserLocationDao
    abstract fun interactionLogDao(): InteractionLogDao
    abstract fun adminAccountDao(): AdminAccountDao
    abstract fun visitStatsDao(): VisitStatsDao
    abstract fun passwordResetDao(): PasswordResetDao

    companion object {
        @Volatile
        private var INSTANCE: SaviaDatabase? = null

        const val DATABASE_NAME = "savia_database.db"

        fun getInstance(context: Context): SaviaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): SaviaDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SaviaDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed data se ejecuta via SaviaApplication con corrutinas
                        Timber.d("SaviaDatabase creada - seed data pendiente")
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    /**
     * Ejecuta seed data completo. Se llama desde SaviaApplication una sola vez.
     */
    fun seedDatabaseAsync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = context.getSharedPreferences("savia_prefs", Context.MODE_PRIVATE)
                val seedVersion = prefs.getInt("seed_version", 0)

                if (seedVersion < SeedData.CURRENT_SEED_VERSION) {
                    Timber.i("Ejecutando seed data v${SeedData.CURRENT_SEED_VERSION}")

                    // Insertar negocios
                    storeDao().insertAll(SeedData.stores)
                    Timber.i("Insertados ${SeedData.stores.size} negocios")

                    // Insertar productos/servicios
                    productDao().insertAll(SeedData.products)
                    Timber.i("Insertados ${SeedData.products.size} productos/servicios")

                    // Insertar admins
                    adminAccountDao().insertAll(SeedData.adminAccounts)
                    Timber.i("Insertados ${SeedData.adminAccounts.size} cuentas admin")

                    // Insertar ubicación por defecto (centro Camagüey)
                    userLocationDao().insert(SeedData.defaultLocation)
                    Timber.i("Ubicación por defecto insertada")

                    // Marcar seed como ejecutado
                    prefs.edit().putInt("seed_version", SeedData.CURRENT_SEED_VERSION).apply()
                    Timber.i("Seed data completado exitosamente")
                } else {
                    Timber.i("Seed data ya existe (versión $seedVersion), omitiendo")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error ejecutando seed data")
            }
        }
    }
}
