package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository: ProductRepository
 * Incluye clasificación de stock (reciente/viejo), búsqueda, filtros.
 * Servicios están exentos de penalización por stock.
 */
class ProductRepository(private val database: SaviaDatabase) {

    private val productDao = database.productDao()

    fun getByStore(tiendaId: String): Flow<List<Product>> = productDao.getByStore(tiendaId)

    suspend fun getByStoreList(tiendaId: String): List<Product> = productDao.getByStoreList(tiendaId)

    suspend fun getById(id: String): Product? = productDao.getById(id)

    suspend fun getAvailableByCategoria(categoria: String): List<Product> = productDao.getAvailableByCategoria(categoria)

    suspend fun getAvailableServices(): List<Product> = productDao.getAvailableServices()

    suspend fun getAllAvailable(): List<Product> = productDao.getAllAvailable()

    suspend fun getFlashOffers(): List<Product> = productDao.getFlashOffers()

    suspend fun getByStoreAndType(tiendaId: String, tipoItem: String): List<Product> = productDao.getByStoreAndType(tiendaId, tipoItem)

    suspend fun search(query: String): List<Product> = productDao.search(query)

    suspend fun updateStock(id: String, estado: String) = productDao.updateStock(id, estado)

    suspend fun updateFlashOffer(id: String, activo: Boolean, precioCUP: Double?, precioUSD: Double?) =
        productDao.updateFlashOffer(id, activo, precioCUP, precioUSD)

    suspend fun updateAllStockTimestamp(tiendaId: String) = productDao.updateAllStockTimestamp(tiendaId)

    suspend fun insert(product: Product) = productDao.insert(product)

    suspend fun update(product: Product) = productDao.update(product)

    suspend fun delete(product: Product) = productDao.delete(product)

    suspend fun deleteByStore(tiendaId: String) = productDao.deleteByStore(tiendaId)

    /**
     * Clasifica stock de un producto:
     * - "reciente": producto actualizado en últimos 30 días
     * - "viejo": producto sin actualizar en >30 días
     * - Servicios siempre son "reciente"
     */
    fun clasificarStock(product: Product): String {
        if (product.tipoItem == "servicio") return "reciente"
        val thirtyDays = 30L * 24 * 60 * 60 * 1000
        return if (product.ultimaActualizacion > System.currentTimeMillis() - thirtyDays) "reciente" else "viejo"
    }

    /**
     * Badge visual para stock:
     * - reciente + disponible → verde
     * - viejo + disponible → dorado (atención)
     * - agotado/no_disponible → rojo
     */
    fun getStockBadge(product: Product): StockBadge {
        return when (product.estadoStock) {
            "agotado", "no_disponible" -> StockBadge.AGOTADO
            else -> {
                val clasificacion = clasificarStock(product)
                if (clasificacion == "reciente") StockBadge.RECENTE else StockBadge.VIEJO
            }
        }
    }

    enum class StockBadge {
        RECENTE,    // Verde: stock actualizado
        VIEJO,      // Dorado: stock sin actualizar >30 días
        AGOTADO     // Rojo: agotado o no disponible
    }
}
