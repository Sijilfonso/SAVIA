package com.savia.camaguey.data.local.dao

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
