package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity: VisitStats (Estadísticas de visitas agregadas por tienda)
 * Se actualiza con triggers o batch desde InteractionLog.
 * Solo la tienda dueña ve sus propias estadísticas.
 */
@Entity(
    tableName = "visit_stats",
    indices = [
        Index(value = ["tiendaId"]),
        Index(value = ["tipo"]),
        Index(value = ["timestampDia"])
    ]
)
data class VisitStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tiendaId: String,
    val productoId: String? = null, // null = visita al perfil
    val tipo: String,               // "perfil" | "producto" | "whatsapp_click"
    val timestampDia: String,       // "YYYY-MM-DD"
    val timestampSemana: String,    // "YYYY-WNN"
    val timestampMes: String,       // "YYYY-MM"
    val timestampAno: String,       // "YYYY"
    val conteo: Int = 1,
    val ultimaActualizacion: Long = System.currentTimeMillis()
)
