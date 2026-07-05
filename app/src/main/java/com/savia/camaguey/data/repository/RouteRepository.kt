package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.util.Haversine

/**
 * Repository: RouteRepository
 * Algoritmo TSP Nearest Neighbor con distancias Haversine.
 * Fallback cuando no hay red. Todo offline.
 */
class RouteRepository(private val database: SaviaDatabase) {

    /**
     * Calcula ruta óptima (TSP Nearest Neighbor) desde ubicación del usuario
     * pasando por todas las tiendas del carrito.
     *
     * @param userLat Latitud usuario
     * @param userLng Longitud usuario
     * @param storeIds Lista de tiendas a visitar (del carrito)
     * @return Lista ordenada de paradas con distancias y tiempos estimados
     */
    suspend fun calculateOptimalRoute(
        userLat: Double,
        userLng: Double,
        storeIds: List<String>,
        modoTransporte: ModoTransporte = ModoTransporte.CAMINANDO
    ): RouteResult {
        val stores = storeIds.mapNotNull { database.storeDao().getById(it) }
        if (stores.isEmpty()) return RouteResult(emptyList(), 0.0, 0)

        // TSP Nearest Neighbor
        val visitados = mutableListOf<StoreStop>()
        val pendientes = stores.toMutableList()
        var currentLat = userLat
        var currentLng = userLng
        var distanciaTotalKm = 0.0

        while (pendientes.isNotEmpty()) {
            val masCercano = pendientes.minByOrNull { store ->
                Haversine.distance(currentLat, currentLng, store.latitud, store.longitud)
            } ?: break

            val distancia = Haversine.distance(currentLat, currentLng, masCercano.latitud, masCercano.longitud)
            distanciaTotalKm += distancia
            visitados.add(StoreStop(masCercano, distancia, modoTransporte))

            currentLat = masCercano.latitud
            currentLng = masCercano.longitud
            pendientes.remove(masCercano)
        }

        val tiempoTotalMin = visitados.sumOf { it.tiempoEstimadoMin }

        return RouteResult(visitados, distanciaTotalKm, tiempoTotalMin)
    }

    /**
     * Calcula distancia directa entre dos puntos (para preview rápido).
     */
    fun distanceBetween(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        return Haversine.distance(lat1, lng1, lat2, lng2)
    }

    enum class ModoTransporte(val velocidadKmh: Double, val label: String) {
        CAMINANDO(4.0, "Caminando"),
        MOTO(30.0, "Moto"),
        AUTO(40.0, "Auto")
    }

    data class StoreStop(
        val store: Store,
        val distanciaDesdeAnteriorKm: Double,
        val modo: ModoTransporte
    ) {
        val tiempoEstimadoMin: Int
            get() = ((distanciaDesdeAnteriorKm / modo.velocidadKmh) * 60).toInt().coerceAtLeast(1)
    }

    data class RouteResult(
        val stops: List<StoreStop>,
        val distanciaTotalKm: Double,
        val tiempoTotalMin: Int
    )
}
