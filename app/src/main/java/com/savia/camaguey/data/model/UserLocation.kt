package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity: UserLocation (Ubicación anónima del comprador)
 * Solo 1 fila en la tabla. Se actualiza manualmente o por GPS.
 * NO se asocia a un usuario identificado.
 */
@Entity(tableName = "user_location")
data class UserLocation(
    @PrimaryKey
    val id: Int = 1,              // Singleton: siempre fila 1
    val latitud: Double,
    val longitud: Double,
    val zona: String?,
    val direccionTexto: String?,
    val modoObtencion: String,    // "gps" | "manual" | "default"
    val actualizadoEn: Long = System.currentTimeMillis()
)
