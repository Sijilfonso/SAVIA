package com.savia.camaguey.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savia.camaguey.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY tiendaId ASC, agregadoEn DESC")
    fun getAll(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items ORDER BY tiendaId ASC, agregadoEn DESC")
    suspend fun getAllList(): List<CartItem>

    @Query("SELECT * FROM cart_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CartItem?

    @Query("SELECT * FROM cart_items WHERE productoId = :productoId LIMIT 1")
    suspend fun getByProductId(productoId: String): CartItem?

    @Query("SELECT * FROM cart_items WHERE tiendaId = :tiendaId")
    suspend fun getByStore(tiendaId: String): List<CartItem>

    @Query("SELECT COUNT(*) FROM cart_items")
    suspend fun count(): Int

    @Query("SELECT SUM(cantidad * precioUnitario) FROM cart_items WHERE moneda = :moneda")
    suspend fun getTotalByCurrency(moneda: String): Double?

    @Query("DELETE FROM cart_items WHERE productoId = :productoId")
    suspend fun deleteByProduct(productoId: String)

    @Query("DELETE FROM cart_items WHERE tiendaId = :tiendaId")
    suspend fun deleteByStore(tiendaId: String)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItem)

    @Update
    suspend fun update(item: CartItem)

    @Delete
    suspend fun delete(item: CartItem)
}
