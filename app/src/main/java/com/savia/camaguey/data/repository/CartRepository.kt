package com.savia.camaguey.data.repository

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
