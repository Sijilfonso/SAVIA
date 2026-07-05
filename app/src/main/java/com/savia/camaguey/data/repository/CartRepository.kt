package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.CartItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository: CartRepository
 * 100% offline. No requiere backend.
 * Agrupado por tienda para mostrar en UI.
 */
class CartRepository(private val database: SaviaDatabase) {

    private val cartDao = database.cartDao()

    fun getAll(): Flow<List<CartItem>> = cartDao.getAll()

    suspend fun getAllList(): List<CartItem> = cartDao.getAllList()

    suspend fun getById(id: Long): CartItem? = cartDao.getById(id)

    suspend fun getByProductId(productoId: String): CartItem? = cartDao.getByProductId(productoId)

    suspend fun getByStore(tiendaId: String): List<CartItem> = cartDao.getByStore(tiendaId)

    suspend fun count(): Int = cartDao.count()

    suspend fun getTotalByCurrency(moneda: String): Double = cartDao.getTotalByCurrency(moneda) ?: 0.0

    /**
     * Añade un producto al carrito. Si ya existe, incrementa cantidad.
     */
    suspend fun addItem(item: CartItem) {
        val existing = cartDao.getByProductId(item.productoId)
        if (existing != null) {
            cartDao.update(existing.copy(cantidad = existing.cantidad + item.cantidad))
        } else {
            cartDao.insert(item)
        }
    }

    suspend fun incrementQuantity(id: Long) {
        val item = cartDao.getById(id) ?: return
        cartDao.update(item.copy(cantidad = item.cantidad + 1))
    }

    suspend fun decrementQuantity(id: Long) {
        val item = cartDao.getById(id) ?: return
        if (item.cantidad > 1) {
            cartDao.update(item.copy(cantidad = item.cantidad - 1))
        } else {
            cartDao.delete(item)
        }
    }

    suspend fun updateItem(item: CartItem) = cartDao.update(item)

    suspend fun deleteItem(item: CartItem) = cartDao.delete(item)

    suspend fun deleteByProduct(productoId: String) = cartDao.deleteByProduct(productoId)

    suspend fun deleteByStore(tiendaId: String) = cartDao.deleteByStore(tiendaId)

    suspend fun clearCart() = cartDao.deleteAll()

    /**
     * Obtiene lista de tiendas únicas en el carrito para agrupar en UI.
     */
    suspend fun getStoresInCart(): List<String> {
        return cartDao.getAllList().map { it.tiendaId }.distinct()
    }

    /**
     * Obtiene items agrupados por tienda para mostrar en RecyclerView.
     */
    suspend fun getItemsGroupedByStore(): Map<String, List<CartItem>> {
        return cartDao.getAllList().groupBy { it.tiendaId }
    }
}
