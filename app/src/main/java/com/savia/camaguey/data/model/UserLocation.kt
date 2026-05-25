package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_locations")
data class UserLocation(
    @PrimaryKey
    val id: Int = 1,
    val latitud: Double,
    val longitud: Double,
    val direccionTexto: String? = null,
    val usaGps: Boolean = false,
    val actualizadoEn: Long = System.currentTimeMillis()
)
