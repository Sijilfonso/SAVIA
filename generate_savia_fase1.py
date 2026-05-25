#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SAVIA — FASE 1 Generator
Genera toda la estructura Android (Kotlin + XML) lista para compilar.
Uso: python3 generate_savia_fase1.py [output_dir]
"""

import os
import sys

OUTPUT_DIR = sys.argv[1] if len(sys.argv) > 1 else "savia-android"
BASE_PKG = "com/savia/camaguey"

def write(path, content):
    full = os.path.join(OUTPUT_DIR, path)
    os.makedirs(os.path.dirname(full), exist_ok=True)
    with open(full, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"[OK] {full}")

# 1. Project build.gradle
write("build.gradle", """// Top-level build file
plugins {
    id 'com.android.application' version '8.1.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.0' apply false
    id 'com.google.devtools.ksp' version '1.9.0-1.0.13' apply false
}
""")

# 2. App build.gradle
write("app/build.gradle", """
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.devtools.ksp'
}

android {
    namespace 'com.savia.camaguey'
    compileSdk 34

    defaultConfig {
        applicationId "com.savia.camaguey"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
    buildFeatures {
        viewBinding true
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.activity:activity-ktx:1.8.0'
    implementation 'androidx.fragment:fragment-ktx:1.6.2'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'
    implementation 'androidx.room:room-runtime:2.6.1'
    implementation 'androidx.room:room-ktx:2.6.1'
    ksp 'androidx.room:room-compiler:2.6.1'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.5'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.5'
    implementation 'org.osmdroid:osmdroid-android:6.1.17'
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    ksp 'com.github.bumptech.glide:compiler:4.16.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'com.jakewharton.timber:timber:5.0.1'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
""".lstrip())

# 3. AndroidManifest.xml
write("app/src/main/AndroidManifest.xml", """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.savia.camaguey">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />

    <application
        android:name=".SaviaApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Savia"
        android:usesCleartextTraffic="true">

        <meta-data
            android:name="org.osmdroid.config"
            android:value="osmdroid" />

        <activity
            android:name=".ui.welcome.WelcomeActivity"
            android:exported="true"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>
</manifest>
""")

# 4. SaviaApplication.kt
write(f"app/src/main/java/{BASE_PKG}/SaviaApplication.kt", """package com.savia.camaguey

import android.app.Application
import androidx.room.Room
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.local.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import timber.log.Timber
import java.io.File

class SaviaApplication : Application() {

    companion object {
        lateinit var database: SaviaDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = File(cacheDir, "osmdroid")
            osmdroidTileCache = File(osmdroidBasePath, "tile")
        }
        database = Room.databaseBuilder(
            applicationContext,
            SaviaDatabase::class.java,
            "savia_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            SeedData.populateDatabase(database)
        }
    }
}
""")

# 5. Constants.kt
write(f"app/src/main/java/{BASE_PKG}/util/Constants.kt", """package com.savia.camaguey.util

object Constants {
    const val DATABASE_NAME = "savia_database"
    const val DATABASE_VERSION = 1
    const val BASE_URL = "https://api.savia.camaguey.cu/"
    const val DEFAULT_LAT = 21.3839
    const val DEFAULT_LNG = -77.9072
    const val STOCK_PENALTY_DAYS = 30
    const val TRIAL_DAYS = 30
    const val HIDE_AFTER_TRIAL_HOURS = 48
    const val VENDOR_PASSWORD_MIN_LENGTH = 8
    const val ADMIN_PASSWORD_MIN_LENGTH = 12
    const val TYPE_MIPYME = "MIPYME"
    const val TYPE_TCP = "TCP"
    const val TYPE_PDL = "PDL"
    const val ITEM_PRODUCT = "producto"
    const val ITEM_SERVICE = "servicio"
    const val CURRENCY_CUP = "CUP"
    const val CURRENCY_USD = "USD"
    const val PLAN_BASIC = "basico"
    const val PLAN_FEATURED = "destacado"
    const val RESET_CODE_EXPIRY_MINUTES = 15
    const val RESET_CODE_LENGTH = 6

    fun translateEntityType(type: String): String {
        return when (type) {
            TYPE_MIPYME -> "Empresa local"
            TYPE_TCP -> "Negocio personal"
            TYPE_PDL -> "Proyecto comunitario"
            else -> "Negocio"
        }
    }
}
""")

# 6. Haversine.kt
write(f"app/src/main/java/{BASE_PKG}/util/Haversine.kt", """package com.savia.camaguey.util

import kotlin.math.*

object Haversine {

    private const val EARTH_RADIUS_KM = 6371.0

    fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    fun distanceFormatted(lat1: Double, lng1: Double, lat2: Double, lng2: Double): String {
        val d = distance(lat1, lng1, lat2, lng2)
        return when {
            d < 1.0 -> "${(d * 1000).toInt()} m"
            d < 10.0 -> "%.1f km".format(d)
            else -> "${d.toInt()} km"
        }
    }
}
""")

# 7. PriceFormatter.kt
write(f"app/src/main/java/{BASE_PKG}/util/PriceFormatter.kt", """package com.savia.camaguey.util

import java.text.NumberFormat
import java.util.Locale

object PriceFormatter {

    private val formatter = NumberFormat.getNumberInstance(Locale("es", "CU"))

    fun format(price: Double, currency: String): String {
        return "\$${formatter.format(price.toInt())} $currency"
    }

    fun formatSimple(price: Double): String {
        return "\$${formatter.format(price.toInt())}"
    }
}
""")

# 8. PasswordValidator.kt
write(f"app/src/main/java/{BASE_PKG}/util/PasswordValidator.kt", """package com.savia.camaguey.util

object PasswordValidator {

    fun isValidVendor(password: String): Boolean {
        if (password.length < Constants.VENDOR_PASSWORD_MIN_LENGTH) return false
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUpper && hasLower && hasDigit
    }

    fun isValidAdmin(password: String): Boolean {
        if (password.length < Constants.ADMIN_PASSWORD_MIN_LENGTH) return false
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        return hasUpper && hasLower && hasDigit && hasSpecial
    }

    fun getVendorError(password: String): String? {
        return when {
            password.length < Constants.VENDOR_PASSWORD_MIN_LENGTH ->
                "Mínimo ${Constants.VENDOR_PASSWORD_MIN_LENGTH} caracteres"
            !password.any { it.isUpperCase() } -> "Debe incluir una mayúscula"
            !password.any { it.isLowerCase() } -> "Debe incluir una minúscula"
            !password.any { it.isDigit() } -> "Debe incluir un número"
            else -> null
        }
    }

    fun getAdminError(password: String): String? {
        return when {
            password.length < Constants.ADMIN_PASSWORD_MIN_LENGTH ->
                "Mínimo ${Constants.ADMIN_PASSWORD_MIN_LENGTH} caracteres"
            !password.any { it.isUpperCase() } -> "Debe incluir una mayúscula"
            !password.any { it.isLowerCase() } -> "Debe incluir una minúscula"
            !password.any { it.isDigit() } -> "Debe incluir un número"
            !password.any { !it.isLetterOrDigit() } -> "Debe incluir un símbolo especial"
            else -> null
        }
    }
}
""")

# 9. SaviaDatabase.kt
write(f"app/src/main/java/{BASE_PKG}/data/local/SaviaDatabase.kt", """package com.savia.camaguey.data.local

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
""")

# 10. Data Models
write(f"app/src/main/java/{BASE_PKG}/data/model/Store.kt", """package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class Store(
    @PrimaryKey
    val id: String,
    val idInterno: String,
    val nombrePublico: String,
    val tipoEntidad: String,
    val zona: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val telefono: String,
    val horario: String,
    val entregaDisponible: Boolean,
    val radioEntregaKm: Int,
    val webUrl: String? = null,
    val planSuscripcion: String,
    val suscripcionActiva: Boolean,
    val fechaRegistro: Long,
    val ultimaConfirmacionStock: Long? = null,
    val verificado: Boolean = false,
    val destacado: Boolean = false,
    val categoriaPrincipal: String,
    val username: String? = null,
    val passwordHash: String? = null,
    val telefonoRecuperacion: String? = null,
    val permiteReservas: Boolean = false,
    val fotoLocalUrl: String? = null,
    val descripcion: String? = null
)
""")

write(f"app/src/main/java/{BASE_PKG}/data/model/Product.kt", """package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Store::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tiendaId"])]
)
data class Product(
    @PrimaryKey
    val id: String,
    val tiendaId: String,
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val moneda: String,
    val tipoItem: String,
    val enStock: Boolean = true,
    val porEncargo: Boolean = false,
    val ofertaFlash: Boolean = false,
    val precioOferta: Double? = null,
    val imagenUrl: String? = null,
    val ultimaEdicion: Long,
    val categoria: String
)
""")

write(f"app/src/main/java/{BASE_PKG}/data/model/CartItem.kt", """package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Store::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productoId"]),
        Index(value = ["tiendaId"])
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productoId: String,
    val tiendaId: String,
    val cantidad: Int = 1,
    val agregadoEn: Long = System.currentTimeMillis()
)
""")

write(f"app/src/main/java/{BASE_PKG}/data/model/UserLocation.kt", """package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_locations")
data class UserLocation(
    @PrimaryKey
    val id: Int = 1,
    val latitud: Double,
    val longitud: Double,
    val direccionTexto: String? = null,
    val usaGps: Boolean = false,
    val actualizadoEn: Long = System.currentTimeMillis()
)
""")

write(f"app/src/main/java/{BASE_PKG}/data/model/InteractionLog.kt", """package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interaction_logs")
data class InteractionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: String,
    val tiendaId: String? = null,
    val productoId: String? = null,
    val queryBusqueda: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
""")

write(f"app/src/main/java/{BASE_PKG}/data/model/AdminAccount.kt", """package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_accounts")
data class AdminAccount(
    @PrimaryKey
    val username: String,
    val passwordHash: String,
    val rol: String = "admin",
    val telefonoRecuperacion: String,
    val creadoEn: Long = System.currentTimeMillis()
)
""")

write(f"app/src/main/java/{BASE_PKG}/data/model/VisitStats.kt", """package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "visit_stats",
    foreignKeys = [
        ForeignKey(
            entity = Store::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["tiendaId"]),
        Index(value = ["productoId"]),
        Index(value = ["fecha"])
    ]
)
data class VisitStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tiendaId: String,
    val productoId: String? = null,
    val tipo: String,
    val fecha: String,
    val timestamp: Long = System.currentTimeMillis()
)
""")

# 11. DAOs
write(f"app/src/main/java/{BASE_PKG}/data/local/dao/StoreDao.kt", """package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores WHERE suscripcionActiva = 1 ORDER BY nombrePublico ASC")
    fun getAllActive(): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Store?

    @Query("SELECT * FROM stores WHERE idInterno = :idInterno LIMIT 1")
    suspend fun getByIdInterno(idInterno: String): Store?

    @Query("SELECT * FROM stores WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): Store?

    @Query("SELECT * FROM stores WHERE zona = :zona AND suscripcionActiva = 1")
    fun getByZona(zona: String): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE categoriaPrincipal = :categoria AND suscripcionActiva = 1")
    fun getByCategoria(categoria: String): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE destacado = 1 AND suscripcionActiva = 1")
    fun getFeatured(): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE tipoEntidad = :tipo AND suscripcionActiva = 1")
    fun getByTipoEntidad(tipo: String): Flow<List<Store>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(store: Store)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stores: List<Store>)

    @Update
    suspend fun update(store: Store)

    @Delete
    suspend fun delete(store: Store)

    @Query("UPDATE stores SET ultimaConfirmacionStock = :timestamp WHERE id = :storeId")
    suspend fun updateStockConfirmation(storeId: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM stores")
    suspend fun count(): Int
}
""")

write(f"app/src/main/java/{BASE_PKG}/data/local/dao/ProductDao.kt", """package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE tiendaId = :tiendaId ORDER BY nombre ASC")
    fun getByStore(tiendaId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Product?

    @Query("SELECT * FROM products WHERE enStock = 1 AND tipoItem = 'producto' ORDER BY ultimaEdicion DESC")
    fun getAllInStock(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE tipoItem = 'servicio' ORDER BY nombre ASC")
    fun getAllServices(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE ofertaFlash = 1 AND enStock = 1")
    fun getFlashOffers(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE categoria = :categoria AND enStock = 1")
    fun getByCategoria(categoria: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE nombre LIKE '%' || :query || '%' AND enStock = 1")
    fun search(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE moneda = :moneda AND enStock = 1")
    fun getByCurrency(moneda: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("DELETE FROM products WHERE tiendaId = :tiendaId")
    suspend fun deleteByStore(tiendaId: String)

    @Query("SELECT COUNT(*) FROM products WHERE tiendaId = :tiendaId")
    suspend fun countByStore(tiendaId: String): Int
}
""")

write(f"app/src/main/java/{BASE_PKG}/data/local/dao/CartDao.kt", """package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY tiendaId, agregadoEn ASC")
    fun getAll(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE productoId = :productoId LIMIT 1")
    suspend fun getByProduct(productoId: String): CartItem?

    @Query("SELECT * FROM cart_items WHERE tiendaId = :tiendaId")
    fun getByStore(tiendaId: String): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItem)

    @Update
    suspend fun update(item: CartItem)

    @Delete
    suspend fun delete(item: CartItem)

    @Query("DELETE FROM cart_items WHERE productoId = :productoId")
    suspend fun deleteByProduct(productoId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM cart_items")
    fun count(): Flow<Int>
}
""")

write(f"app/src/main/java/{BASE_PKG}/data/local/dao/UserLocationDao.kt", """package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.UserLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLocationDao {
    @Query("SELECT * FROM user_locations WHERE id = 1 LIMIT 1")
    fun get(): Flow<UserLocation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: UserLocation)

    @Update
    suspend fun update(location: UserLocation)

    @Query("DELETE FROM user_locations")
    suspend fun clear()
}
""")

write(f"app/src/main/java/{BASE_PKG}/data/local/dao/InteractionLogDao.kt", """package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.InteractionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionLogDao {
    @Query("SELECT * FROM interaction_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<InteractionLog>>

    @Query("SELECT * FROM interaction_logs WHERE tipo = :tipo ORDER BY timestamp DESC")
    fun getByType(tipo: String): Flow<List<InteractionLog>>

    @Insert
    suspend fun insert(log: InteractionLog)

    @Query("DELETE FROM interaction_logs WHERE timestamp < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
}
""")

write(f"app/src/main/java/{BASE_PKG}/data/local/dao/AdminAccountDao.kt", """package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.AdminAccount

@Dao
interface AdminAccountDao {
    @Query("SELECT * FROM admin_accounts WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): AdminAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AdminAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AdminAccount>)

    @Update
    suspend fun update(account: AdminAccount)

    @Delete
    suspend fun delete(account: AdminAccount)
}
""")

write(f"app/src/main/java/{BASE_PKG}/data/local/dao/VisitStatsDao.kt", """package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.VisitStats
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitStatsDao {
    @Query("SELECT * FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'perfil' ORDER BY timestamp DESC")
    fun getProfileVisits(tiendaId: String): Flow<List<VisitStats>>

    @Query("SELECT * FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'producto' ORDER BY timestamp DESC")
    fun getProductVisits(tiendaId: String): Flow<List<VisitStats>>

    @Query("SELECT COUNT(*) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'perfil' AND fecha = :fecha")
    suspend fun countProfileVisitsToday(tiendaId: String, fecha: String): Int

    @Query("SELECT COUNT(*) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'producto' AND fecha = :fecha")
    suspend fun countProductVisitsToday(tiendaId: String, fecha: String): Int

    @Query("SELECT COUNT(*) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'perfil'")
    suspend fun countTotalProfileVisits(tiendaId: String): Int

    @Query("SELECT COUNT(*) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'producto'")
    suspend fun countTotalProductVisits(tiendaId: String): Int

    @Insert
    suspend fun insert(stat: VisitStats)

    @Query("DELETE FROM visit_stats WHERE timestamp < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
}
""")

# 12. Repositories
write(f"app/src/main/java/{BASE_PKG}/data/repository/StoreRepository.kt", """package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.dao.StoreDao
import com.savia.camaguey.data.model.Store
import kotlinx.coroutines.flow.Flow

class StoreRepository(private val storeDao: StoreDao) {

    fun getAllActive(): Flow<List<Store>> = storeDao.getAllActive()
    fun getFeatured(): Flow<List<Store>> = storeDao.getFeatured()
    fun getByZona(zona: String): Flow<List<Store>> = storeDao.getByZona(zona)
    fun getByCategoria(categoria: String): Flow<List<Store>> = storeDao.getByCategoria(categoria)
    fun getByTipoEntidad(tipo: String): Flow<List<Store>> = storeDao.getByTipoEntidad(tipo)

    suspend fun getById(id: String): Store? = storeDao.getById(id)
    suspend fun getByIdInterno(idInterno: String): Store? = storeDao.getByIdInterno(idInterno)
    suspend fun getByUsername(username: String): Store? = storeDao.getByUsername(username)

    suspend fun insert(store: Store) = storeDao.insert(store)
    suspend fun insertAll(stores: List<Store>) = storeDao.insertAll(stores)
    suspend fun update(store: Store) = storeDao.update(store)
    suspend fun delete(store: Store) = storeDao.delete(store)

    suspend fun confirmStock(storeId: String) {
        storeDao.updateStockConfirmation(storeId, System.currentTimeMillis())
    }

    suspend fun count(): Int = storeDao.count()
}
""")

write(f"app/src/main/java/{BASE_PKG}/data/repository/ProductRepository.kt", """package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.dao.ProductDao
import com.savia.camaguey.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getByStore(tiendaId: String): Flow<List<Product>> = productDao.getByStore(tiendaId)
    fun getAllInStock(): Flow<List<Product>> = productDao.getAllInStock()
    fun getAllServices(): Flow<List<Product>> = productDao.getAllServices()
    fun getFlashOffers(): Flow<List<Product>> = productDao.getFlashOffers()
    fun getByCategoria(categoria: String): Flow<List<Product>> = productDao.getByCategoria(categoria)
    fun search(query: String): Flow<List<Product>> = productDao.search(query)
    fun getByCurrency(moneda: String): Flow<List<Product>> = productDao.getByCurrency(moneda)

    suspend fun getById(id: String): Product? = productDao.getById(id)
    suspend fun insert(product: Product) = productDao.insert(product)
    suspend fun insertAll(products: List<Product>) = productDao.insertAll(products)
    suspend fun update(product: Product) = productDao.update(product)
    suspend fun delete(product: Product) = productDao.delete(product)
    suspend fun deleteByStore(tiendaId: String) = productDao.deleteByStore(tiendaId)
    suspend fun countByStore(tiendaId: String): Int = productDao.countByStore(tiendaId)
}
""")

write(f"app/src/main/java/{BASE_PKG}/data/repository/CartRepository.kt", """package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.dao.CartDao
import com.savia.camaguey.data.model.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    fun getAll(): Flow<List<CartItem>> = cartDao.getAll()
    fun getByStore(tiendaId: String): Flow<List<CartItem>> = cartDao.getByStore(tiendaId)
    fun count(): Flow<Int> = cartDao.count()

    suspend fun add(productoId: String, tiendaId: String) {
        val existing = cartDao.getByProduct(productoId)
        if (existing != null) {
            cartDao.update(existing.copy(cantidad = existing.cantidad + 1))
        } else {
            cartDao.insert(CartItem(productoId = productoId, tiendaId = tiendaId, cantidad = 1))
        }
    }

    suspend fun removeOne(productoId: String) {
        val existing = cartDao.getByProduct(productoId) ?: return
        if (existing.cantidad > 1) {
            cartDao.update(existing.copy(cantidad = existing.cantidad - 1))
        } else {
            cartDao.deleteByProduct(productoId)
        }
    }

    suspend fun remove(productoId: String) = cartDao.deleteByProduct(productoId)
    suspend fun clear() = cartDao.clearAll()
}
""")

# 13. SeedData.kt
write(f"app/src/main/java/{BASE_PKG}/data/local/SeedData.kt", """package com.savia.camaguey.data.local

import com.savia.camaguey.data.model.*
import com.savia.camaguey.util.Constants

object SeedData {

    suspend fun populateDatabase(db: SaviaDatabase) {
        if (db.storeDao().count() > 0) return

        val stores = generateStores()
        db.storeDao().insertAll(stores)

        val products = generateProducts(stores)
        db.productDao().insertAll(products)

        val admins = generateAdmins()
        db.adminAccountDao().insertAll(admins)
    }

    private fun generateStores(): List<Store> {
        val now = System.currentTimeMillis()
        return listOf(
            Store(
                id = "store_001", idInterno = "CMP-00001", nombrePublico = "La Bodega de Pepe",
                tipoEntidad = Constants.TYPE_TCP, zona = "La Caridad",
                direccion = "Calle Maceo #45 e/ Libertad y Agramonte", latitud = 21.3769, longitud = -77.9172,
                telefono = "+53551234567", horario = "Lun-Dom 8:00-20:00",
                entregaDisponible = true, radioEntregaKm = 3, webUrl = "https://bodegapepe.ejemplo.cu",
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Alimentos",
                username = "bodegapepe", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53551234567", permiteReservas = false
            ),
            Store(
                id = "store_002", idInterno = "CMP-00002", nombrePublico = "El Mercadito de María",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "La Caridad",
                direccion = "Av. de los Mártires #112", latitud = 21.3775, longitud = -77.9165,
                telefono = "+53552345678", horario = "Lun-Sab 7:00-19:00",
                entregaDisponible = true, radioEntregaKm = 2,
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Alimentos",
                username = "mercaditomaria", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53552345678"
            ),
            Store(
                id = "store_003", idInterno = "CMP-00003", nombrePublico = "Ferretería El Clavo",
                tipoEntidad = Constants.TYPE_TCP, zona = "Centro Histórico",
                direccion = "Calle Ignacio Agramonte #78", latitud = 21.3834, longitud = -77.9181,
                telefono = "+53553456789", horario = "Lun-Sab 8:00-17:00",
                entregaDisponible = false, radioEntregaKm = 0,
                webUrl = "https://ferreclavo.ejemplo.cu",
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = false, categoriaPrincipal = "Ferretería",
                username = "ferreclavo", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53553456789"
            ),
            Store(
                id = "store_004", idInterno = "CMP-00004", nombrePublico = "La Casa del Aseo",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "Centro Histórico",
                direccion = "Calle República #234", latitud = 21.3840, longitud = -77.9175,
                telefono = "+53554567890", horario = "Lun-Dom 9:00-18:00",
                entregaDisponible = true, radioEntregaKm = 4,
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Aseo",
                username = "casadelaseo", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53554567890"
            ),
            Store(
                id = "store_005", idInterno = "CMP-00005", nombrePublico = "Electrosur",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "Vista Hermosa",
                direccion = "Calle 3ra #456 e/ 8 y 10", latitud = 21.3720, longitud = -77.9100,
                telefono = "+53555678901", horario = "Lun-Sab 9:00-18:00",
                entregaDisponible = true, radioEntregaKm = 5,
                webUrl = "https://electrosur.ejemplo.cu",
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Electrónica",
                username = "electrosur", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53555678901"
            ),
            Store(
                id = "store_006", idInterno = "CMP-00006", nombrePublico = "Carnicería El Buen Corte",
                tipoEntidad = Constants.TYPE_TCP, zona = "Santa Rosa",
                direccion = "Calle 5ta #89", latitud = 21.3890, longitud = -77.9050,
                telefono = "+53556789012", horario = "Lun-Sab 6:00-14:00",
                entregaDisponible = true, radioEntregaKm = 2,
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = false, categoriaPrincipal = "Carnes",
                username = "buencorte", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53556789012"
            ),
            Store(
                id = "store_007", idInterno = "CMP-00007", nombrePublico = "Despensa Santa Rosa",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "Santa Rosa",
                direccion = "Av. Santa Rosa #301", latitud = 21.3880, longitud = -77.9060,
                telefono = "+53557890123", horario = "Lun-Dom 7:00-21:00",
                entregaDisponible = true, radioEntregaKm = 3,
                webUrl = "https://despensasantarosa.ejemplo.cu",
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Alimentos",
                username = "despensasantarosa", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53557890123"
            ),
            Store(
                id = "store_008", idInterno = "CMP-00008", nombrePublico = "Ferretería La Herradura",
                tipoEntidad = Constants.TYPE_TCP, zona = "Centro Histórico",
                direccion = "Calle Cisneros #55", latitud = 21.3825, longitud = -77.9190,
                telefono = "+53558901234", horario = "Lun-Sab 8:00-17:00",
                entregaDisponible = false, radioEntregaKm = 0,
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = false, categoriaPrincipal = "Ferretería",
                username = "herradura", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53558901234"
            ),
            Store(
                id = "store_009", idInterno = "CMP-00009", nombrePublico = "Belleza Tropical",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "Vista Hermosa",
                direccion = "Calle 4ta #202", latitud = 21.3730, longitud = -77.9110,
                telefono = "+53559012345", horario = "Mar-Dom 10:00-19:00",
                entregaDisponible = true, radioEntregaKm = 4,
                webUrl = "https://bellezatropical.ejemplo.cu",
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = null,
                verificado = true, destacado = true, categoriaPrincipal = "Servicios",
                username = "bellezatropical", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53559012345"
            ),
            Store(
                id = "store_010", idInterno = "CMP-00010", nombrePublico = "Reparaciones Rápidas",
                tipoEntidad = Constants.TYPE_TCP, zona = "La Caridad",
                direccion = "Calle 10 de Octubre #12", latitud = 21.3780, longitud = -77.9150,
                telefono = "+53550123456", horario = "Lun-Sab 8:00-18:00",
                entregaDisponible = true, radioEntregaKm = 6,
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = null,
                verificado = true, destacado = false, categoriaPrincipal = "Servicios",
                username = "reparaciones", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53550123456"
            ),
            Store(
                id = "store_011", idInterno = "CMP-00011", nombrePublico = "Vivero Comunitario Santa Elena",
                tipoEntidad = Constants.TYPE_PDL, zona = "Santa Elena",
                direccion = "Calle Santa Elena #77", latitud = 21.3750, longitud = -77.9200,
                telefono = "+53551234999", horario = "Lun-Sab 8:00-16:00",
                entregaDisponible = true, radioEntregaKm = 3,
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = null,
                verificado = true, destacado = false, categoriaPrincipal = "Servicios",
                username = "viverosantaelena", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53551234999"
            )
        )
    }

    private fun generateProducts(stores: List<Store>): List<Product> {
        val now = System.currentTimeMillis()
        val products = mutableListOf<Product>()
        var productId = 1

        val bodega = stores[0]
        val bodegaProducts = listOf(
            "Arroz" to 180.0, "Frijoles" to 250.0, "Aceite" to 450.0, "Azúcar" to 120.0,
            "Harina" to 90.0, "Leche" to 65.0, "Huevos (30u)" to 450.0, "Pollo (kg)" to 380.0,
            "Pasta" to 85.0, "Sal" to 25.0, "Café" to 320.0, "Spaghetti" to 95.0,
            "Sardinas" to 180.0, "Galletas" to 120.0, "Mantequilla" to 280.0
        )
        bodegaProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = bodega.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Alimentos"
            ))
        }

        val mercadito = stores[1]
        val mercaditoProducts = listOf(
            "Arroz premium" to 220.0, "Frijoles negros" to 280.0, "Aceite de oliva" to 650.0,
            "Azúcar blanca" to 140.0, "Harina de trigo" to 100.0, "Leche condensada" to 120.0,
            "Huevos (12u)" to 200.0, "Pavo (kg)" to 520.0, "Pasta corta" to 95.0,
            "Sal marina" to 40.0, "Café molido" to 380.0, "Spaghetti integral" to 110.0,
            "Atún" to 250.0, "Galletas saladas" to 140.0, "Mantequilla sin sal" to 300.0,
            "Queso blanco" to 350.0, "Yogurt" to 85.0
        )
        mercaditoProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = mercadito.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Alimentos"
            ))
        }

        val ferre = stores[2]
        val ferreProducts = listOf(
            "Martillo" to 350.0, "Clavos (kg)" to 180.0, "Destornillador" to 120.0,
            "Cinta métrica" to 95.0, "Alicate" to 220.0, "Broca" to 85.0,
            "Silicona" to 150.0, "Pintura blanca" to 450.0, "Brocha" to 80.0,
            "Cable eléctrico (m)" to 65.0, "Enchufe" to 45.0, "Bombillo LED" to 120.0,
            "Cerradura" to 380.0, "Bisagras (par)" to 95.0, "Lija" to 35.0,
            "Tornillos (caja)" to 75.0, "Guantes trabajo" to 140.0
        )
        ferreProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = ferre.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Ferretería"
            ))
        }

        val aseo = stores[3]
        val aseoProducts = listOf(
            "Detergente" to 250.0, "Jabón de baño" to 65.0, "Shampoo" to 180.0,
            "Acondicionador" to 190.0, "Pasta dental" to 120.0, "Cepillo dental" to 85.0,
            "Papel higiénico" to 150.0, "Servilletas" to 95.0, "Desinfectante" to 220.0,
            "Limpiador multiuso" to 180.0, "Esponja" to 35.0, "Cloro" to 140.0,
            "Suavizante" to 200.0, "Jabón líquido" to 160.0, "Toallas húmedas" to 110.0,
            "Ambientador" to 130.0, "Bolsa basura" to 75.0
        )
        aseoProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = aseo.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Aseo"
            ))
        }

        val electro = stores[4]
        val electroProducts = listOf(
            "Cable HDMI" to 450.0, "Mouse USB" to 350.0, "Teclado" to 650.0,
            "Pendrive 32GB" to 380.0, "Batería AA (par)" to 120.0, "Cargador universal" to 550.0,
            "Auriculares" to 280.0, "Hub USB" to 220.0, "Adaptador corriente" to 180.0,
            "Batería 9V" to 95.0, "Cable red (m)" to 85.0, "Interruptor wifi" to 750.0,
            "Regleta" to 320.0, "Linterna LED" to 180.0, "Pila recargable" to 250.0,
            "Cable USB-C" to 150.0, "Adaptador OTG" to 120.0
        )
        electroProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = electro.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Electrónica"
            ))
        }

        val carne = stores[5]
        val carneProducts = listOf(
            "Bistec (kg)" to 850.0, "Carne molida (kg)" to 780.0, "Pollo entero" to 520.0,
            "Chuleta (kg)" to 920.0, "Costilla (kg)" to 680.0, "Hígado (kg)" to 450.0,
            "Pechuga (kg)" to 950.0, "Alitas (kg)" to 380.0, "Pierna (kg)" to 720.0,
            "Filete pescado (kg)" to 650.0, "Salchichas (paq)" to 280.0, "Jamón (kg)" to 1200.0,
            "Tocino (kg)" to 580.0, "Morcilla (kg)" to 420.0, "Chorizo (kg)" to 750.0,
            "Lomo (kg)" to 1100.0, "Paletilla (kg)" to 680.0
        )
        carneProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = carne.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Carnes"
            ))
        }

        val despensa = stores[6]
        val despensaProducts = listOf(
            "Arroz" to 190.0, "Frijoles" to 260.0, "Aceite" to 460.0, "Azúcar" to 125.0,
            "Harina" to 95.0, "Leche" to 70.0, "Huevos (30u)" to 460.0, "Pollo (kg)" to 390.0,
            "Pasta" to 90.0, "Sal" to 28.0, "Café" to 330.0, "Spaghetti" to 100.0,
            "Sardinas" to 185.0, "Galletas" to 125.0, "Mantequilla" to 290.0,
            "Queso crema" to 320.0, "Mermelada" to 180.0
        )
        despensaProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = despensa.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Alimentos"
            ))
        }

        val herradura = stores[7]
        val herraduraProducts = listOf(
            "Martillo carpintero" to 380.0, "Clavos galvanizados (kg)" to 200.0, "Destornillador plano" to 130.0,
            "Cinta métrica 5m" to 110.0, "Alicate universal" to 240.0, "Broca metal" to 95.0,
            "Silicona caliente" to 160.0, "Pintura exterior" to 480.0, "Brocha 4\"" to 90.0,
            "Cable coaxial (m)" to 75.0, "Tomacorriente" to 55.0, "Bombillo ahorrador" to 130.0,
            "Cerradura digital" to 420.0, "Bisagras grandes" to 110.0, "Lija agua" to 40.0,
            "Tornillos inox (caja)" to 85.0, "Guantes cuero" to 160.0
        )
        herraduraProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = herradura.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Ferretería"
            ))
        }

        val belleza = stores[8]
        val bellezaServices = listOf(
            "Corte dama" to 350.0, "Corte caballero" to 250.0, "Tinte" to 650.0,
            "Manicure" to 280.0, "Pedicure" to 320.0, "Facial" to 450.0,
            "Maquillaje social" to 550.0, "Peinado evento" to 480.0, "Depilación cejas" to 120.0,
            "Tratamiento keratina" to 1200.0, "Uñas acrílicas" to 450.0, "Masaje relajante" to 500.0,
            "Limpieza facial profunda" to 380.0, "Alisado permanente" to 850.0, "Mechas" to 750.0,
            "Maquillaje novia" to 950.0, "Peinado trenzas" to 350.0
        )
        bellezaServices.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = belleza.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_SERVICE,
                enStock = true, porEncargo = true, ultimaEdicion = now, categoria = "Belleza"
            ))
        }

        val repar = stores[9]
        val reparServices = listOf(
            "Reparación celular pantalla" to 2500.0, "Reparación celular batería" to 1800.0,
            "Reparación celular puerto carga" to 1200.0, "Reparación laptop" to 3500.0,
            "Formateo PC" to 800.0, "Instalación antivirus" to 450.0,
            "Reparación nevera" to 2800.0, "Reparación lavadora" to 2200.0,
            "Reparación ventilador" to 650.0, "Reparación TV" to 3200.0,
            "Cambio módulo celular" to 4500.0, "Reparación tablet" to 2000.0,
            "Mantenimiento PC" to 600.0, "Recuperación datos" to 1500.0,
            "Reparación consola" to 3800.0, "Soldadura electrónica" to 850.0,
            "Diagnóstico gratuito" to 0.0
        )
        reparServices.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = repar.id, nombre = name, precio = price,
                moneda = if (price > 0) Constants.CURRENCY_CUP else Constants.CURRENCY_CUP,
                tipoItem = Constants.ITEM_SERVICE,
                enStock = true, porEncargo = true, ultimaEdicion = now, categoria = "Reparación"
            ))
        }

        val vivero = stores[10]
        val viveroItems = listOf(
            "Planta ornamental" to 250.0, "Semillas variadas" to 120.0,
            "Maceta mediana" to 180.0, "Tierra abonada" to 150.0,
            "Asesoría jardinería" to 500.0, "Compostaje casero" to 300.0,
            "Taller ambiental" to 200.0, "Planta medicinal" to 180.0,
            "Fertilizante orgánico" to 220.0, "Árbol frutal" to 450.0
        )
        viveroItems.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = vivero.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP,
                tipoItem = if (name.contains("Asesoría") || name.contains("Taller") || name.contains("Compostaje"))
                    Constants.ITEM_SERVICE else Constants.ITEM_PRODUCT,
                enStock = true, porEncargo = false, ultimaEdicion = now, categoria = "Jardinería"
            ))
        }

        return products
    }

    private fun generateAdmins(): List<AdminAccount> {
        return listOf(
            AdminAccount(
                username = "savia.admin1",
                passwordHash = "\$2a\$12\$placeholder_hash_admin1",
                rol = "admin",
                telefonoRecuperacion = "+53559998877"
            ),
            AdminAccount(
                username = "savia.admin2",
                passwordHash = "\$2a\$12\$placeholder_hash_admin2",
                rol = "admin",
                telefonoRecuperacion = "+53557776655"
            )
        )
    }
}
""")

# 14. Resources
write("app/src/main/res/values/strings.xml", """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">SAVIA</string>
    <string name="app_slogan">Camagüey crece aquí</string>
    <string name="btn_start_shopping">Comenzar a comprar</string>
    <string name="link_business_access">¿Tienes un negocio? Accede aquí</string>
    <string name="search_hint">¿Qué buscas hoy?</string>
    <string name="filter_all">Todo</string>
    <string name="filter_food">Alimentos</string>
    <string name="filter_cleaning">Aseo</string>
    <string name="filter_hardware">Ferretería</string>
    <string name="filter_cup">CUP</string>
    <string name="filter_usd">USD</string>
    <string name="filter_today">Hoy</string>
    <string name="filter_deals">Gangas</string>
    <string name="featured">Destacados</string>
    <string name="catalog">Catálogo</string>
    <string name="contact_whatsapp">Escribir por WhatsApp</string>
    <string name="in_stock">En stock</string>
    <string name="on_order">Por encargo</string>
    <string name="service">Servicio</string>
    <string name="available">Disponible</string>
    <string name="not_available">No disponible</string>
    <string name="add_to_cart">Añadir</string>
    <string name="cart">Carrito</string>
    <string name="route">Ruta</string>
    <string name="settings">Ajustes</string>
    <string name="home">Inicio</string>
    <string name="map">Mapa</string>
    <string name="login">Ingresar</string>
    <string name="username">Usuario</string>
    <string name="password">Contraseña</string>
    <string name="forgot_password">¿Olvidaste tu contraseña?</string>
    <string name="create_account">Crear cuenta nueva</string>
    <string name="panel_control">Panel de Control</string>
    <string name="summary">Resumen</string>
    <string name="config">Config</string>
    <string name="confirm_stock">Confirmar stock al día</string>
    <string name="days_without_confirm">Llevas %1$d días sin confirmar</string>
    <string name="views">Vistas</string>
    <string name="products_count">Productos</string>
    <string name="clicks_wa">Clicks WA</string>
    <string name="subscription">Suscripción</string>
    <string name="active">Activa</string>
    <string name="quick_actions">Acciones Rápidas</string>
    <string name="add_product">Añadir Producto</string>
    <string name="flash_offer">Oferta Flash</string>
    <string name="download_pdf">Descargar PDF</string>
    <string name="view_stats">Ver Estadísticas</string>
    <string name="verified">Verificado</string>
    <string name="featured_badge">Destacado</string>
    <string name="community_project">Proyecto comunitario</string>
    <string name="local_business">Empresa local</string>
    <string name="personal_business">Negocio personal</string>
    <string name="delivery_available">Entrega disponible · Radio %1$d km</string>
    <string name="no_reservations">Sin reservas</string>
    <string name="business_hours">Horario de atención</string>
    <string name="mon_sun">Lun-Dom</string>
    <string name="get_gps">Obtener mi ubicación GPS</string>
    <string name="continue_btn">Continuar</string>
    <string name="explore_without_number">Explorar sin número →</string>
    <string name="enter_whatsapp">Tu número de WhatsApp</string>
    <string name="all">Todo</string>
</resources>
""")

write("app/src/main/res/values/styles.xml", """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Widget.Savia.Card" parent="Widget.MaterialComponents.CardView">
        <item name="cardCornerRadius">16dp</item>
        <item name="cardElevation">2dp</item>
        <item name="contentPadding">0dp</item>
        <item name="cardBackgroundColor">@color/bg_card</item>
    </style>

    <style name="Widget.Savia.Chip" parent="Widget.MaterialComponents.Chip.Choice">
        <item name="chipBackgroundColor">@color/bg_card</item>
        <item name="chipStrokeColor">@color/border_light</item>
        <item name="chipStrokeWidth">1dp</item>
        <item name="chipCornerRadius">20dp</item>
        <item name="android:textColor">@color/text_primary</item>
    </style>

    <style name="Widget.Savia.Chip.Active" parent="Widget.MaterialComponents.Chip.Choice">
        <item name="chipBackgroundColor">@color/primary_dark</item>
        <item name="chipStrokeWidth">0dp</item>
        <item name="chipCornerRadius">20dp</item>
        <item name="android:textColor">@color/white</item>
    </style>

    <style name="Widget.Savia.Badge.Green" parent="Widget.MaterialComponents.TextView">
        <item name="android:textColor">@color/white</item>
        <item name="android:background">@drawable/bg_badge_green</item>
        <item name="android:paddingStart">8dp</item>
        <item name="android:paddingEnd">8dp</item>
        <item name="android:paddingTop">2dp</item>
        <item name="android:paddingBottom">2dp</item>
        <item name="android:textSize">11sp</item>
    </style>

    <style name="Widget.Savia.Badge.Gold" parent="Widget.MaterialComponents.TextView">
        <item name="android:textColor">@color/white</item>
        <item name="android:background">@drawable/bg_badge_gold</item>
        <item name="android:paddingStart">8dp</item>
        <item name="android:paddingEnd">8dp</item>
        <item name="android:paddingTop">2dp</item>
        <item name="android:paddingBottom">2dp</item>
        <item name="android:textSize">11sp</item>
    </style>

    <style name="Widget.Savia.Badge.Red" parent="Widget.MaterialComponents.TextView">
        <item name="android:textColor">@color/white</item>
        <item name="android:background">@drawable/bg_badge_red</item>
        <item name="android:paddingStart">8dp</item>
        <item name="android:paddingEnd">8dp</item>
        <item name="android:paddingTop">2dp</item>
        <item name="android:paddingBottom">2dp</item>
        <item name="android:textSize">11sp</item>
    </style>

    <style name="Widget.Savia.Badge.Blue" parent="Widget.MaterialComponents.TextView">
        <item name="android:textColor">@color/white</item>
        <item name="android:background">@drawable/bg_badge_blue</item>
        <item name="android:paddingStart">8dp</item>
        <item name="android:paddingEnd">8dp</item>
        <item name="android:paddingTop">2dp</item>
        <item name="android:paddingBottom">2dp</item>
        <item name="android:textSize">11sp</item>
    </style>
</resources>
""")

write("app/src/main/res/values/dimens.xml", """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="padding_horizontal">16dp</dimen>
    <dimen name="card_spacing">12dp</dimen>
    <dimen name="card_corner">16dp</dimen>
    <dimen name="button_corner">12dp</dimen>
    <dimen name="input_corner">12dp</dimen>
    <dimen name="chip_corner">20dp</dimen>
    <dimen name="bottom_nav_height">56dp</dimen>
    <dimen name="title_size">20sp</dimen>
    <dimen name="subtitle_size">16sp</dimen>
    <dimen name="body_size">14sp</dimen>
    <dimen name="price_size">16sp</dimen>
    <dimen name="caption_size">12sp</dimen>
</resources>
""")

# 15. Drawables
write("app/src/main/res/drawable/bg_badge_green.xml", """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/badge_stock_recent" />
    <corners android:radius="12dp" />
</shape>
""")

write("app/src/main/res/drawable/bg_badge_gold.xml", """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/gold" />
    <corners android:radius="12dp" />
</shape>
""")

write("app/src/main/res/drawable/bg_badge_red.xml", """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/badge_stock_old" />
    <corners android:radius="12dp" />
</shape>
""")

write("app/src/main/res/drawable/bg_badge_blue.xml", """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/badge_verified" />
    <corners android:radius="12dp" />
</shape>
""")

# 16. WelcomeActivity stub
write(f"app/src/main/java/{BASE_PKG}/ui/welcome/WelcomeActivity.kt", """package com.savia.camaguey.ui.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.savia.camaguey.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }
}
""")

write("app/src/main/res/layout/activity_welcome.xml", """<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/primary_dark">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:orientation="vertical"
        android:gravity="center"
        android:padding="24dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="SAVIA"
            android:textColor="@color/white"
            android:textSize="48sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/app_slogan"
            android:textColor="@color/white"
            android:textSize="18sp"
            android:layout_marginTop="8dp" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnStart"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="48dp"
            android:text="@string/btn_start_shopping"
            style="@style/Widget.MaterialComponents.Button"
            android:backgroundTint="@color/white"
            android:textColor="@color/primary_dark" />

        <TextView
            android:id="@+id/tvBusinessLink"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:text="@string/link_business_access"
            android:textColor="@color/white"
            android:textSize="14sp" />
    </LinearLayout>
</FrameLayout>
""")

# 17. ProGuard
write("app/proguard-rules.pro", """# ProGuard rules for SAVIA
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# OSMDroid
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
""")

# 18. Gradle wrapper
write("gradle/wrapper/gradle-wrapper.properties", """distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.2-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
""")

# 19. settings.gradle
write("settings.gradle", """pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Savia"
include ':app'
""")

print("\\n" + "="*60)
print(f"✅ SAVIA FASE 1 generado en: {os.path.abspath(OUTPUT_DIR)}")
print("="*60)
print("""
INSTRUCCIONES:
1. Abre la carpeta generada en Android Studio
2. Sincroniza Gradle (Sync Project with Gradle Files)
3. Conecta un dispositivo o emulador (API 21+)
4. Ejecuta Run 'app'

ESTRUCTURA GENERADA:
├── build.gradle
├── settings.gradle
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/savia/camaguey/
│       │   ├── SaviaApplication.kt
│       │   ├── ui/welcome/WelcomeActivity.kt
│       │   ├── data/
│       │   │   ├── model/ (7 entities)
│       │   │   ├── local/
│       │   │   │   ├── SaviaDatabase.kt
│       │   │   │   ├── SeedData.kt
│       │   │   │   └── dao/ (7 DAOs)
│       │   │   └── repository/ (3 repos)
│       │   └── util/
│       │       ├── Constants.kt
│       │       ├── Haversine.kt
│       │       ├── PriceFormatter.kt
│       │       └── PasswordValidator.kt
│       └── res/
│           ├── values/
│           │   ├── colors.xml
│           │   ├── themes.xml
│           │   ├── strings.xml
│           │   ├── styles.xml
│           │   └── dimens.xml
│           ├── drawable/
│           │   └── bg_badge_*.xml (4)
│           └── layout/
│               └── activity_welcome.xml
""")
