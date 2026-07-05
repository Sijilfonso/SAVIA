package com.savia.camaguey.util

import kotlin.math.*

/**
 * Utilidad: Haversine formula para distancias geográficas.
 * Incluye cálculo de TSP Nearest Neighbor y tiempos estimados por modo transporte.
 * Optimizado para API 21 (sin java.lang.Math adicionales de APIs superiores).
 */
object Haversine {

    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Distancia entre dos puntos en kilómetros usando fórmula Haversine.
     * Compatible API 21: usa Math.toRadians y Math.sin/cos/atan2/sqrt.
     */
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Distancia formateada para UI: "1.2 km" o "450 m".
     */
    fun distanceFormatted(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        val km = distance(lat1, lon1, lat2, lon2)
        return when {
            km < 0.1 -> "${(km * 1000).toInt()} m"
            km < 1.0 -> "${(km * 10).toInt() / 10.0} km"
            else -> "${(km * 10).toInt() / 10.0} km"
        }
    }

    /**
     * TSP Nearest Neighbor: ordena lista de puntos desde origen.
     * @return Lista de índices ordenados (0-based, referencia a la lista de entrada).
     */
    fun tspNearestNeighbor(
        originLat: Double,
        originLng: Double,
        points: List<Pair<Double, Double>>
    ): List<Int> {
        if (points.isEmpty()) return emptyList()

        val remaining = points.indices.toMutableList()
        val route = mutableListOf<Int>()
        var currentLat = originLat
        var currentLng = originLng

        while (remaining.isNotEmpty()) {
            var nearestIndex = remaining[0]
            var nearestDist = Double.MAX_VALUE

            for (idx in remaining) {
                val dist = distance(currentLat, currentLng, points[idx].first, points[idx].second)
                if (dist < nearestDist) {
                    nearestDist = dist
                    nearestIndex = idx
                }
            }

            route.add(nearestIndex)
            remaining.remove(nearestIndex)
            currentLat = points[nearestIndex].first
            currentLng = points[nearestIndex].second
        }

        return route
    }

    /**
     * Tiempo estimado de viaje en minutos.
     * Velocidades: caminando 4 km/h, moto 30 km/h, auto 40 km/h.
     */
    fun estimatedTimeMinutes(distanciaKm: Double, modo: String): Int {
        val velocidadKmh = when (modo.lowercase()) {
            "caminando", "walking" -> 4.0
            "moto", "motorcycle" -> 30.0
            "auto", "car" -> 40.0
            else -> 4.0
        }
        val horas = distanciaKm / velocidadKmh
        return (horas * 60).toInt().coerceAtLeast(1)
    }

    /**
     * Formatea tiempo estimado para UI: "15 min", "1h 20min".
     */
    fun formatTime(minutes: Int): String {
        return if (minutes < 60) {
            "$minutes min"
        } else {
            val h = minutes / 60
            val m = minutes % 60
            if (m == 0) "${h}h" else "${h}h ${m}min"
        }
    }
}
