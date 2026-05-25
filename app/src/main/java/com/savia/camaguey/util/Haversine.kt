package com.savia.camaguey.util

import kotlin.math.*

object Haversine {

    private const val EARTH_RADIUS_KM = 6371.0

    fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    fun distanceFormatted(lat1: Double, lng1: Double, lat2: Double, lng2: Double): String {
        val d = distance(lat1, lng1, lat2, lng2)
        return when {
            d < 1.0 -> "${(d * 1000).toInt()} m"
            d < 10.0 -> "%.1f km".format(d)
            else -> "${d.toInt()} km"
        }
    }
}
