package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interaction_logs")
data class InteractionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: String,
    val tiendaId: String? = null,
    val productoId: String? = null,
    val queryBusqueda: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
