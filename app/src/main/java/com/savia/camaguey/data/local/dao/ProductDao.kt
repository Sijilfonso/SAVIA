package com.savia.camaguey.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savia.camaguey.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE tiendaId = :tiendaId ORDER BY nombre ASC")
    fun getByStore(tiendaId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE tiendaId = :tiendaId ORDER BY nombre ASC")
    suspend fun getByStoreList(tiendaId: String): List<Product>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Product?

    @Query("SELECT * FROM products WHERE categoria = :categoria AND tipoItem = 'producto' AND estadoStock != 'agotado'")
    suspend fun getAvailableByCategoria(categoria: String): List<Product>

    @Query("SELECT * FROM products WHERE tipoItem = 'servicio' AND estadoStock != 'no_disponible'")
    suspend fun getAvailableServices(): List<Product>

    @Query("SELECT * FROM products WHERE estadoStock != 'agotado' AND estadoStock != 'no_disponible'")
    suspend fun getAllAvailable(): List<Product>

    @Query("SELECT * FROM products WHERE ofertaFlash = 1 AND estadoStock != 'agotado'")
    suspend fun getFlashOffers(): List<Product>

    @Query("SELECT * FROM products WHERE nombre LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<Product>

    @Query("SELECT * FROM products WHERE tiendaId = :tiendaId AND tipoItem = :tipoItem")
    suspend fun getByStoreAndType(tiendaId: String, tipoItem: String): List<Product>

    @Query("UPDATE products SET ultimaActualizacion = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE products SET estadoStock = :estado WHERE id = :id")
    suspend fun updateStock(id: String, estado: String)

    @Query("UPDATE products SET ofertaFlash = :activo, precioOfertaCUP = :precioCUP, precioOfertaUSD = :precioUSD WHERE id = :id")
    suspend fun updateFlashOffer(id: String, activo: Boolean, precioCUP: Double?, precioUSD: Double?)

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

    @Query("UPDATE products SET ultimaActualizacion = :timestamp WHERE tiendaId = :tiendaId")
    suspend fun updateAllStockTimestamp(tiendaId: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}
