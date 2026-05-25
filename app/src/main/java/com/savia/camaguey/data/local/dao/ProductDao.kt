package com.savia.camaguey.data.local.dao

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
