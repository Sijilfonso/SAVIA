package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.util.Haversine
import kotlinx.coroutines.flow.Flow

/**
 * Repository: StoreRepository
 * Incluye ranking de búsqueda con scoring: distancia + destacado + stock + oferta + random.
 */
class StoreRepository(private val database: SaviaDatabase) {

    private val storeDao = database.storeDao()

    fun getAllApproved(): Flow<List<Store>> = storeDao.getAllApproved()

    suspend fun getAllApprovedList(): List<Store> = storeDao.getAllApprovedList()

    suspend fun getById(id: String): Store? = storeDao.getById(id)

    suspend fun getByIdInterno(idInterno: String): Store? = storeDao.getByIdInterno(idInterno)

    suspend fun getByUsername(username: String): Store? = storeDao.getByUsername(username)

    suspend fun getByZona(zona: String): List<Store> = storeDao.getByZona(zona)

    suspend fun getByCategoria(categoria: String): List<Store> = storeDao.getByCategoria(categoria)

    suspend fun getFeatured(): List<Store> = storeDao.getFeatured()

    suspend fun getPendingVerification(): List<Store> = storeDao.getPendingVerification()

    suspend fun getAllZonas(): List<String> = storeDao.getAllZonas()

    suspend fun getAllCategorias(): List<String> = storeDao.getAllCategorias()

    suspend fun countApproved(): Int = storeDao.countApproved()

    suspend fun insert(store: Store) = storeDao.insert(store)

    suspend fun update(store: Store) = storeDao.update(store)

    suspend fun delete(store: Store) = storeDao.delete(store)

    /**
     * Ranking score = (distancia_km * -10) + (planDestacado * 50) + (stockReciente * 30) + (ofertaFlash * 20) + (random * 5)
     * Orden descendente. Filtros aplicados antes de scorear.
     */
    suspend fun searchWithRanking(
        query: String? = null,
        zona: String? = null,
        categoria: String? = null,
        userLat: Double? = null,
        userLng: Double? = null,
        soloDestacados: Boolean = false,
        soloConStockReciente: Boolean = false,
        soloOfertas: Boolean = false
    ): List<ScoredStore> {
        val stores = storeDao.getAllApprovedList()
        val now = System.currentTimeMillis()
        val thirtyDays = 30L * 24 * 60 * 60 * 1000

        // Obtener productos para calcular stock reciente y ofertas flash por tienda
        val products = database.productDao().getAllAvailable()
        val productosPorTienda = products.groupBy { it.tiendaId }

        val filtered = stores.filter { store ->
            val matchQuery = query?.let { q ->
                store.nombrePublico.contains(q, ignoreCase = true) ||
                store.direccionCompleta.contains(q, ignoreCase = true) ||
                store.categoriaPrincipal.contains(q, ignoreCase = true)
            } ?: true

            val matchZona = zona?.let { store.zona == it } ?: true
            val matchCategoria = categoria?.let { store.categoriaPrincipal == it } ?: true
            val matchDestacado = if (soloDestacados) store.planDestacado else true

            val productosTienda = productosPorTienda[store.id] ?: emptyList()
            val matchStock = if (soloConStockReciente) {
                productosTienda.any { it.ultimaActualizacion > (now - thirtyDays) || it.tipoItem == "servicio" }
            } else true

            val matchOferta = if (soloOfertas) {
                productosTienda.any { it.ofertaFlash }
            } else true

            matchQuery && matchZona && matchCategoria && matchDestacado && matchStock && matchOferta
        }

        return filtered.map { store ->
            val distanciaKm = if (userLat != null && userLng != null) {
                Haversine.distance(userLat, userLng, store.latitud, store.longitud)
            } else 0.0

            val productosTienda = productosPorTienda[store.id] ?: emptyList()
            val tieneStockReciente = productosTienda.any {
                it.ultimaActualizacion > (now - thirtyDays) || it.tipoItem == "servicio"
            }
            val tieneOferta = productosTienda.any { it.ofertaFlash }

            val score = (distanciaKm * -10.0) +
                    (if (store.planDestacado) 50 else 0) +
                    (if (tieneStockReciente) 30 else 0) +
                    (if (tieneOferta) 20 else 0) +
                    (Math.random() * 5)

            ScoredStore(store, score, distanciaKm)
        }.sortedByDescending { it.score }
    }

    data class ScoredStore(
        val store: Store,
        val score: Double,
        val distanciaKm: Double
    )
}
